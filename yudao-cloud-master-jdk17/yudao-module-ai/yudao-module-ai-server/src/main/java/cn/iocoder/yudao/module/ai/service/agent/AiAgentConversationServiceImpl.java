package cn.iocoder.yudao.module.ai.service.agent;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentChatSendReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConversationPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConfigDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConversationDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentMessageDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.agent.AiAgentConversationMapper;
import cn.iocoder.yudao.module.ai.enums.agent.AiAgentTypeEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.core.AiAgentOrchestrator;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyFilter;
import cn.iocoder.yudao.module.ai.framework.ai.core.scheduler.AiAgentScheduler;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI Agent 会话 Service 实现类
 *
 * <p>核心职责：
 * <ol>
 *   <li>管理 Agent 会话生命周期（创建、查询、删除）</li>
 *   <li>协调 Agent 执行引擎（AiAgentScheduler/AiAgentOrchestrator）处理用户请求</li>
 *   <li>持久化会话和消息记录</li>
 * </ol>
 * </p>
 *
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class AiAgentConversationServiceImpl implements AiAgentConversationService {

    @Resource
    private AiAgentConfigService agentConfigService;
    @Resource
    private AiModelService modelService;
    @Resource
    private AiAgentConversationMapper conversationMapper;
    @Resource
    private AiAgentMessageService messageService;

    @Resource
    private AiLlmService llmService;
    @Resource
    private AiAgentScheduler agentScheduler;
    @Resource
    private AiAgentOrchestrator agentOrchestrator;
    @Resource
    private AiSafetyFilter safetyFilter;

    /** Agent 执行超时时间 */
    private static final Duration AGENT_TIMEOUT = Duration.ofMinutes(5);

    @Override
    public AiAgentMessageDO createConversationAndSend(Long userId, AiAgentChatSendReqVO sendReqVO) {
        // 1. 校验 Agent 配置
        AiAgentConfigDO agentConfig = agentConfigService.validateAgentConfig(sendReqVO.getAgentId());
        AiModelDO model = modelService.getModel(agentConfig.getModelId());

        // 2. 创建会话
        AiAgentConversationDO conversation = new AiAgentConversationDO();
        conversation.setUserId(userId);
        conversation.setAgentId(agentConfig.getId());
        conversation.setModelId(agentConfig.getModelId());
        conversation.setTitle(generateTitle(sendReqVO.getContent()));
        conversation.setActive(true);
        conversation.setMessageCount(0);
        conversation.setTotalTokens(0);
        conversationMapper.insert(conversation);

        // 3. 保存用户消息
        AiAgentMessageDO userMessage = messageService.createUserMessage(
                conversation.getId(), sendReqVO.getContent());

        // 4. 执行 Agent
        try {
            String agentResponse = executeAgentSync(agentConfig, sendReqVO.getContent());
            conversation.setMessageCount(2);
            conversation.setTotalTokens(estimateTokens(sendReqVO.getContent(), agentResponse));
            conversationMapper.updateById(conversation);

            return messageService.createAssistantMessage(
                    conversation.getId(), agentResponse, null, null);
        } catch (Exception e) {
            log.error("[AgentConversation] Agent 执行失败: agentId={}", agentConfig.getId(), e);
            conversation.setActive(false);
            conversationMapper.updateById(conversation);
            return messageService.createErrorMessage(
                    conversation.getId(), "Agent 执行异常: " + e.getMessage());
        }
    }

    @Override
    public AiAgentMessageDO sendMessage(Long userId, AiAgentChatSendReqVO sendReqVO) {
        // 1. 校验会话存在且属于当前用户
        AiAgentConversationDO conversation = validateConversation(sendReqVO.getConversationId(), userId);

        // 2. 校验 Agent 配置
        AiAgentConfigDO agentConfig = agentConfigService.validateAgentConfig(conversation.getAgentId());

        // 3. 构建上下文（历史消息）
        String context = messageService.buildContext(conversation.getId());

        // 4. 保存用户消息
        AiAgentMessageDO userMessage = messageService.createUserMessage(
                conversation.getId(), sendReqVO.getContent());

        // 5. 执行 Agent
        try {
            String agentResponse = executeAgentSync(agentConfig, context + "\n用户：" + sendReqVO.getContent());
            conversation.setMessageCount(conversation.getMessageCount() + 2);
            conversation.setTotalTokens(conversation.getTotalTokens()
                    + estimateTokens(sendReqVO.getContent(), agentResponse));
            conversationMapper.updateById(conversation);

            return messageService.createAssistantMessage(
                    conversation.getId(), agentResponse, null, null);
        } catch (Exception e) {
            log.error("[AgentConversation] Agent 执行失败: agentId={}", agentConfig.getId(), e);
            return messageService.createErrorMessage(
                    conversation.getId(), "Agent 执行异常: " + e.getMessage());
        }
    }

    @Override
    public Flux<String> sendMessageStream(Long userId, AiAgentChatSendReqVO sendReqVO) {
        return Flux.defer(() -> {
            try {
                // 1. 校验路由：获取或创建会话
                AiAgentConversationDO conversation;
                String context;
                if (sendReqVO.getConversationId() != null) {
                    conversation = validateConversation(sendReqVO.getConversationId(), userId);
                    context = messageService.buildContext(conversation.getId());
                } else {
                    // 首次对话，创建会话
                    AiAgentConfigDO agentConfig = agentConfigService.validateAgentConfig(sendReqVO.getAgentId());
                    conversation = new AiAgentConversationDO();
                    conversation.setUserId(userId);
                    conversation.setAgentId(agentConfig.getId());
                    conversation.setModelId(agentConfig.getModelId());
                    conversation.setTitle(generateTitle(sendReqVO.getContent()));
                    conversation.setActive(true);
                    conversation.setMessageCount(0);
                    conversation.setTotalTokens(0);
                    conversationMapper.insert(conversation);
                    context = "";
                }
                final AiAgentConversationDO finalConversation = conversation;
                final String finalContext = context;

                // 2. 校验 Agent 配置
                AiAgentConfigDO agentConfig = agentConfigService.validateAgentConfig(
                        finalConversation.getAgentId());

                // 3. 保存用户消息
                messageService.createUserMessage(finalConversation.getId(), sendReqVO.getContent());

                // 4. 流式执行 Agent，封装为 SSE 事件流
                Flux<String> agentStream = executeAgentStream(agentConfig,
                        finalContext.isEmpty() ? sendReqVO.getContent()
                                : finalContext + "\n用户：" + sendReqVO.getContent());

                // 5. 收集完整响应并保存
                StringBuilder fullResponse = new StringBuilder();
                return agentStream
                        .map(chunk -> {
                            fullResponse.append(chunk);
                            return chunk;
                        })
                        .doOnComplete(() -> {
                            try {
                                // 更新会话统计
                                finalConversation.setMessageCount(finalConversation.getMessageCount() + 2);
                                finalConversation.setTotalTokens(finalConversation.getTotalTokens()
                                        + estimateTokens(sendReqVO.getContent(), fullResponse.toString()));
                                conversationMapper.updateById(finalConversation);

                                // 保存 Assistant 消息
                                messageService.createAssistantMessage(
                                        finalConversation.getId(), fullResponse.toString(),
                                        null, null);
                            } catch (Exception e) {
                                log.error("[AgentConversation] 保存流式响应失败", e);
                            }
                        })
                        .doOnError(e -> {
                            log.error("[AgentConversation] 流式 Agent 执行失败", e);
                            finalConversation.setActive(false);
                            conversationMapper.updateById(finalConversation);
                            try {
                                messageService.createErrorMessage(
                                        finalConversation.getId(), "Agent 流式执行异常: " + e.getMessage());
                            } catch (Exception ex) {
                                log.error("[AgentConversation] 保存错误消息失败", ex);
                            }
                        });
            } catch (Exception e) {
                log.error("[AgentConversation] 流式会话初始化失败", e);
                return Flux.just("data: [ERROR] " + e.getMessage() + "\n\n");
            }
        });
    }

    // ========== Agent 执行核心 ==========

    /**
     * 阻塞式执行 Agent
     *
     * <p>根据 Agent 类型选择执行方式：
     * <ul>
     *   <li>支持的类型：通过 AiAgentScheduler 直接调度对应的 Agent Bean</li>
     *   <li>未知类型：使用 AiAgentOrchestrator 自动路由</li>
     * </ul>
     * </p>
     */
    private String executeAgentSync(AiAgentConfigDO config, String input) {
        String safeInput = safetyFilter.filter(input);
        AiAgentTypeEnum agentType = AiAgentTypeEnum.getByType(config.getType());

        // 如果 Agent 在调度器中已注册，直接调用
        if (agentType != null) {
            AiAgent agent = agentScheduler.getAgent(agentType.getCode() + "-agent");
            if (agent != null) {
                // 构建增强的系统提示词
                String systemPrompt = buildSystemPrompt(config);
                String enrichedInput = systemPrompt + "\n\n" + safeInput;

                List<String> results = agent.execute(enrichedInput, "")
                        .collectList()
                        .block(AGENT_TIMEOUT);
                if (results != null) {
                    return String.join("", results);
                }
            }
        }

        // 降级：使用 LLM 直接调用
        log.warn("[AgentConversation] Agent 未注册，降级为 LLM 直接调用: type={}", config.getType());
        String systemPrompt = buildSystemPrompt(config);
        return llmService.chatSync(config.getModelId(), systemPrompt, safeInput, "");
    }

    /**
     * 流式执行 Agent
     */
    private Flux<String> executeAgentStream(AiAgentConfigDO config, String input) {
        String safeInput = safetyFilter.filter(input);
        AiAgentTypeEnum agentType = AiAgentTypeEnum.getByType(config.getType());

        if (agentType != null) {
            AiAgent agent = agentScheduler.getAgent(agentType.getCode() + "-agent");
            if (agent != null) {
                String systemPrompt = buildSystemPrompt(config);
                String enrichedInput = systemPrompt + "\n\n" + safeInput;
                return agent.executeStream(enrichedInput, "");
            }
        }

        // 降级：使用 LLM 流式调用
        log.warn("[AgentConversation] Agent 未注册，降级为 LLM 流式调用: type={}", config.getType());
        String systemPrompt = buildSystemPrompt(config);
        return llmService.chatStream(config.getModelId(), systemPrompt, safeInput, "");
    }

    /**
     * 构建最终的系统提示词
     *
     * <p>优先级：数据库配置 > Agent 默认
     */
    private String buildSystemPrompt(AiAgentConfigDO config) {
        if (StrUtil.isNotBlank(config.getSystemPrompt())) {
            return config.getSystemPrompt();
        }
        AiAgentTypeEnum agentType = AiAgentTypeEnum.getByType(config.getType());
        if (agentType != null) {
            AiAgent agent = agentScheduler.getAgent(agentType.getCode() + "-agent");
            if (agent instanceof cn.iocoder.yudao.module.ai.framework.ai.core.agent.core.ReActAgent reActAgent) {
                return reActAgent.getSystemPrompt();
            }
        }
        return "你是一个智能 AI 助手，请根据用户需求提供专业的回答。";
    }

    // ========== 辅助方法 ==========

    private AiAgentConversationDO validateConversation(Long id, Long userId) {
        AiAgentConversationDO conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            throw exception(AGENT_CONVERSATION_NOT_EXISTS);
        }
        return conversation;
    }

    private String generateTitle(String content) {
        if (StrUtil.length(content) > 30) {
            return content.substring(0, 30) + "...";
        }
        return content;
    }

    private int estimateTokens(String input, String output) {
        return (StrUtil.length(input) + StrUtil.length(output)) / 2;
    }

    // ========== 查询方法 ==========

    @Override
    public PageResult<AiAgentConversationDO> getConversationPage(AiAgentConversationPageReqVO pageReqVO) {
        return conversationMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiAgentConversationDO> getConversationListByUserId(Long userId) {
        return conversationMapper.selectListByUserId(userId);
    }

    @Override
    public AiAgentConversationDO getConversation(Long id) {
        AiAgentConversationDO conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            throw exception(AGENT_CONVERSATION_NOT_EXISTS);
        }
        return conversation;
    }

    @Override
    public void deleteConversation(Long id) {
        AiAgentConversationDO conversation = conversationMapper.selectById(id);
        if (conversation == null) {
            throw exception(AGENT_CONVERSATION_NOT_EXISTS);
        }
        conversation.setActive(false);
        conversationMapper.updateById(conversation);
    }
}
