package cn.iocoder.yudao.module.ai.util;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.moonshot.MoonshotChatOptions;
import org.springaicommunity.qianfan.QianFanChatOptions;
import org.springframework.ai.anthropic.AnthropicChatOptions;
import org.springframework.ai.azure.openai.AzureOpenAiChatOptions;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.minimax.MiniMaxChatOptions;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.zhipuai.ZhiPuAiChatOptions;
import reactor.core.publisher.Flux;

import java.util.*;
import java.util.function.Function;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;

/**
 * Spring AI 工具类
 *
 * @author 芋道源码
 */
@Slf4j
public class AiUtils {

    public static final String TOOL_CONTEXT_LOGIN_USER = "LOGIN_USER";
    public static final String TOOL_CONTEXT_TENANT_ID = "TENANT_ID";

    public static ChatOptions buildChatOptions(AiPlatformEnum platform, String model, Double temperature, Integer maxTokens) {
        return buildChatOptions(platform, model, temperature, maxTokens, null, null);
    }

    public static ChatOptions buildChatOptions(AiPlatformEnum platform, String model, Double temperature, Integer maxTokens,
                                               List<ToolCallback> toolCallbacks, Map<String, Object> toolContext) {
        toolCallbacks = ObjUtil.defaultIfNull(toolCallbacks, Collections.emptyList());
        toolContext = ObjUtil.defaultIfNull(toolContext, Collections.emptyMap());
        // noinspection EnhancedSwitchMigration
        switch (platform) {
            case TONG_YI:
                return DashScopeChatOptions.builder().withModel(model).withTemperature(temperature).withMaxToken(maxTokens)
                        .withEnableThinking(true) // TODO 芋艿：默认都开启 thinking 模式，后续可以让用户配置
                        .withToolCallbacks(toolCallbacks).withToolContext(toolContext).build();
            case YI_YAN:
                return QianFanChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens).build();
            case DEEP_SEEK:
            case DOU_BAO: // 复用 DeepSeek 客户端
            case HUN_YUAN: // 复用 DeepSeek 客户端
            case SILICON_FLOW: // 复用 DeepSeek 客户端
            case XING_HUO: // 复用 DeepSeek 客户端
                return DeepSeekChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case ZHI_PU:
                return ZhiPuAiChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case MINI_MAX:
                return MiniMaxChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case MOONSHOT:
                return MoonshotChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case OPENAI:
            case GEMINI: // 复用 OpenAI 客户端
            case BAI_CHUAN: // 复用 OpenAI 客户端
            case GROK: // 复用 OpenAI 客户端
                return OpenAiChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case AZURE_OPENAI:
                return AzureOpenAiChatOptions.builder().deploymentName(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case ANTHROPIC:
                return AnthropicChatOptions.builder().model(model).temperature(temperature).maxTokens(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            case OLLAMA:
                return OllamaChatOptions.builder().model(model).temperature(temperature).numPredict(maxTokens)
                        .toolCallbacks(toolCallbacks).toolContext(toolContext).build();
            default:
                throw new IllegalArgumentException(StrUtil.format("未知平台({})", platform));
        }
    }

    public static Message buildMessage(String type, String content) {
        if (MessageType.USER.getValue().equals(type)) {
            return new UserMessage(content);
        }
        if (MessageType.ASSISTANT.getValue().equals(type)) {
            return new AssistantMessage(content);
        }
        if (MessageType.SYSTEM.getValue().equals(type)) {
            return new SystemMessage(content);
        }
        if (MessageType.TOOL.getValue().equals(type)) {
            throw new UnsupportedOperationException("暂不支持 tool 消息：" + content);
        }
        throw new IllegalArgumentException(StrUtil.format("未知消息类型({})", type));
    }

    public static Map<String, Object> buildCommonToolContext() {
        Map<String, Object> context = new HashMap<>();
        context.put(TOOL_CONTEXT_LOGIN_USER, SecurityFrameworkUtils.getLoginUser());
        context.put(TOOL_CONTEXT_TENANT_ID, TenantContextHolder.getTenantId());
        return context;
    }

    @SuppressWarnings("ConstantValue")
    public static String getChatResponseContent(ChatResponse response) {
        if (response == null
                || response.getResult() == null
                || response.getResult().getOutput() == null) {
            return null;
        }
        return response.getResult().getOutput().getText();
    }

    @SuppressWarnings("ConstantValue")
    public static String getChatResponseReasoningContent(ChatResponse response) {
        if (response == null
                || response.getResult() == null
                || response.getResult().getOutput() == null) {
            return null;
        }
        if (response.getResult().getOutput() instanceof DeepSeekAssistantMessage) {
            return ((DeepSeekAssistantMessage) (response.getResult().getOutput())).getReasoningContent();
        }
        return null;
    }

    /**
     * 构建带有模型 fallback 链的流式 Flux。
     * 从列表末尾（最高优先级）开始尝试，如果某模型流失败则自动切换到下一个模型。
     *
     * @param models         已启用的模型列表（按 sort 排序）
     * @param streamBuilder  为单个模型创建 Flux 流的函数
     * @param logPrefix      日志前缀（用于区分不同业务场景）
     * @param finalErrorCode 所有模型均失败时返回的错误码
     * @return 带有 fallback 的流式 Flux
     */
    public static Flux<CommonResult<String>> buildStreamWithFallback(
            List<AiModelDO> models,
            Function<AiModelDO, Flux<CommonResult<String>>> streamBuilder,
            String logPrefix,
            ErrorCode finalErrorCode) {
        Flux<CommonResult<String>> result = null;
        for (int i = models.size() - 1; i >= 0; i--) {
            AiModelDO model = models.get(i);
            Flux<CommonResult<String>> stream;
            try {
                stream = streamBuilder.apply(model);
            } catch (Exception e) {
                log.warn("[{}][模型({}) 创建流失败，跳过]", logPrefix, model.getName(), e);
                continue;
            }
            if (result == null) {
                result = stream;
            } else {
                Flux<CommonResult<String>> current = stream;
                Flux<CommonResult<String>> next = result;
                result = current.onErrorResume(e -> {
                    log.warn("[{}][模型({}) 失败，尝试下一个]", logPrefix, model.getName(), e);
                    return next;
                });
            }
        }
        return result != null ? result.onErrorResume(error -> {
            log.error("[{}][所有模型均失败]", logPrefix, error);
            return Flux.just(error(finalErrorCode));
        }) : Flux.just(error(finalErrorCode));
    }

}