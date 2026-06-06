package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourceGenerateReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourcePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningResourceDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiLearningResourceMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class AiLearningResourceServiceImpl implements AiLearningResourceService {

    @Resource
    private AiLearningResourceMapper learningResourceMapper;
    @Resource
    private AiModelService modelService;
    @Resource
    private AiSystemConfigService configService;

    private String getSystemPrompt(String resourceType) {
        // 优先使用管理端配置的全局 Prompt
        String customPrompt = configService.getConfigValue("edu.resource.prompt", null);
        if (StrUtil.isNotBlank(customPrompt)) {
            return customPrompt + "\n\n资源类型：" + resourceType;
        }
        // 回退到各类型的默认 Prompt
        return switch (resourceType.toUpperCase()) {
            case "DOCUMENT" -> """
                    你是一位课程讲解文档撰写专家。根据用户提供的课程主题和难度级别，生成详细、结构化的课程讲解文档。
                    输出格式为 Markdown，包含：1. 课程概述 2. 核心概念讲解 3. 详细知识点 4. 示例说明 5. 重点总结。
                    需要根据难度级别调整内容深度：BEGINNER-入门级, INTERMEDIATE-进阶级, ADVANCED-高级。
                    """;
            case "MIND_MAP" -> """
                    你是一位思维导图生成专家。将用户提供的课程主题整理成清晰的思维导图结构。
                    输出格式为 Markdown 标题层级（# ## ### ####），展示知识体系结构。
                    """;
            case "EXERCISE" -> """
                    你是一位习题生成专家。根据课程主题和难度，生成分层练习题。
                    输出格式：包含选择题、填空题、简答题等类型，每题附带答案和解析。
                    按难度分级：基础题(60%)、提高题(30%)、挑战题(10%)。
                    """;
            case "READING" -> """
                    你是一位学术阅读推荐专家。根据课程主题，推荐拓展阅读材料。
                    输出格式：推荐5-8篇相关文献/资料，包含标题、作者、核心内容摘要和推荐理由。
                    """;
            case "CODE_EXAMPLE" -> """
                    你是一位编程教学专家。根据课程主题生成代码实操案例。
                    输出格式：1. 案例描述 2. 学习目标 3. 完整代码实现 4. 代码解析 5. 运行结果 6. 扩展练习。
                    代码需要带注释说明。
                    """;
            default -> """
                    你是一位教育资源生成专家。根据用户提供的主题和要求，生成优质的学习资源内容。
                    输出格式为 Markdown，内容需结构清晰、准确详实。
                    """;
        };
    }

    @Override
    public Flux<CommonResult<String>> generateResource(LearningResourceGenerateReqVO reqVO, Long userId) {
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            log.error("[generateResource][userId({}) 无可用模型]", userId);
            return Flux.just(error(MODEL_DEFAULT_NOT_EXISTS));
        }

        AiLearningResourceDO resource = BeanUtils.toBean(reqVO, AiLearningResourceDO.class);
        resource.setUserId(userId).setStatus("GENERATING").setTitle(reqVO.getTopic());
        learningResourceMapper.insert(resource);

        Long resourceId = resource.getId();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(getSystemPrompt(reqVO.getResourceType())));
        String userPrompt = StrUtil.format("请生成关于「{}」的{}资源。{}",
                reqVO.getTopic(), reqVO.getResourceType(), 
                StrUtil.blankToDefault(reqVO.getRequirements(), ""));
        if (StrUtil.isNotBlank(reqVO.getDifficulty())) {
            userPrompt += "\n难度级别：" + reqVO.getDifficulty();
        }
        messages.add(new UserMessage(userPrompt));

        Flux<CommonResult<String>> result = null;
        for (int i = models.size() - 1; i >= 0; i--) {
            AiModelDO model = models.get(i);
            ChatModel chatModel = modelService.getChatModel(model.getId());
            AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
            ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                    model.getTemperature(), model.getMaxTokens());
            Prompt prompt = new Prompt(messages, options);

            StringBuffer contentBuffer = new StringBuffer();
            Flux<CommonResult<String>> stream = chatModel.stream(prompt).map(chunk -> {
                String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : "";
                if (newContent == null || "null".equals(newContent)) newContent = "";
                contentBuffer.append(newContent);
                return success(newContent);
            }).doOnComplete(() -> {
                TenantUtils.executeIgnore(() -> {
                    learningResourceMapper.updateById(new AiLearningResourceDO()
                            .setId(resourceId).setContent(contentBuffer.toString()).setStatus("COMPLETED"));
                });
            }).doOnError(throwable -> {
                log.error("[generateResource][reqVO({}) 异常]", reqVO, throwable);
                TenantUtils.executeIgnore(() -> {
                    learningResourceMapper.updateById(new AiLearningResourceDO()
                            .setId(resourceId).setErrorMessage(throwable.getMessage()).setStatus("FAILED"));
                });
            });

            if (result == null) {
                result = stream;
            } else {
                Flux<CommonResult<String>> current = stream;
                Flux<CommonResult<String>> next = result;
                result = current.onErrorResume(e -> {
                    log.warn("[generateResource][模型({}) 失败，尝试下一个]", model.getName(), e);
                    return next;
                });
            }
        }
        return result != null ? result.onErrorResume(error -> {
            log.error("[generateResource][userId({}) 所有模型均失败]", userId, error);
            return Flux.just(error(EDUCATION_STREAM_ERROR));
        }) : Flux.just(error(EDUCATION_STREAM_ERROR));
    }

    @Override
    public AiLearningResourceDO getResource(Long id) {
        return learningResourceMapper.selectById(id);
    }

    @Override
    public PageResult<AiLearningResourceDO> getResourcePage(LearningResourcePageReqVO reqVO) {
        return learningResourceMapper.selectPage(reqVO);
    }

    @Override
    public void deleteResource(Long id) {
        if (learningResourceMapper.selectById(id) == null) {
            throw exception(LEARNING_RESOURCE_NOT_EXISTS);
        }
        learningResourceMapper.deleteById(id);
    }
}
