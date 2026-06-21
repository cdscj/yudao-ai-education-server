package cn.iocoder.yudao.module.ai.framework.ai.core.gateway;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiCallDetailDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.cache.AiSemanticCache;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.AiModelFactory;
import cn.iocoder.yudao.module.ai.framework.ai.core.observability.AiCallMetrics;
import cn.iocoder.yudao.module.ai.framework.ai.core.observability.AiModelPricing;
import cn.iocoder.yudao.module.ai.framework.ai.core.observability.AiTraceContext;
import cn.iocoder.yudao.module.ai.service.config.AiApiCallDetailService;
import cn.iocoder.yudao.module.ai.service.model.AiApiKeyService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * AI 模型网关 — 统一模型调用入口，提供重试、熔断、fallback、健康检查能力
 *
 * <p>与 AiLlmService 的关系：AiLlmService 负责 Prompt 构建和业务上下文，
 * AiModelGateway 负责模型路由、容错和高可用。两者通过委托模式协作。</p>
 *
 * @author yudao
 */
@Service
@Slf4j
public class AiModelGateway {

    @Resource
    private AiModelService modelService;
    @Resource
    private AiApiKeyService apiKeyService;
    @Resource
    private AiModelFactory modelFactory;
    @Resource
    private AiModelHealthChecker healthChecker;
    @Resource
    private AiCallMetrics callMetrics;
    @Resource
    private AiApiCallDetailService callDetailService;
    @Resource
    private AiSemanticCache semanticCache;

    // ========== 熔断器状态 ==========

    /** 每个模型的连续失败计数 (key: modelId) */
    private final ConcurrentHashMap<Long, AtomicInteger> failureCounters = new ConcurrentHashMap<>();
    /** 每个模型的熔断打开时间 (key: modelId, value: 熔断结束时间戳) */
    private final ConcurrentHashMap<Long, Long> circuitOpenTimestamps = new ConcurrentHashMap<>();

    private static final int DEFAULT_FAILURE_THRESHOLD = 5;
    private static final long DEFAULT_CIRCUIT_OPEN_MS = 30_000;
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_RETRY_BACKOFF_MS = 1000;

    // ========== 公开 API ==========

    /**
     * 同步调用模型，带重试 + 熔断 + fallback
     */
    public String chatSync(Long modelId, String systemPrompt, String userInput, String context) {
        AiModelDO model = modelService.validateModel(modelId);
        return executeWithRetryAndFallback(model, systemPrompt, userInput, context, false);
    }

    /**
     * 流式调用模型，带重试 + 熔断 + fallback
     */
    public Flux<String> chatStream(Long modelId, String systemPrompt, String userInput, String context) {
        AiModelDO model = modelService.validateModel(modelId);
        return executeStreamWithRetryAndFallback(model, systemPrompt, userInput, context);
    }

    /**
     * 同步调用模型，使用 Prompt 对象（用于复杂 Prompt 场景）
     */
    public String chatSync(Long modelId, Prompt prompt) {
        AiModelDO model = modelService.validateModel(modelId);
        return executePromptWithRetryAndFallback(model, prompt);
    }

    /**
     * 流式调用模型，使用 Prompt 对象
     */
    public Flux<String> chatStream(Long modelId, Prompt prompt) {
        AiModelDO model = modelService.validateModel(modelId);
        return executePromptStreamWithRetryAndFallback(model, prompt);
    }

    // ========== 同步调用核心 ==========

    private String executeWithRetryAndFallback(AiModelDO model, String systemPrompt,
                                                String userInput, String context, boolean isFallback) {
        // 0. 语义缓存检查（仅非 fallback 场景）
        if (!isFallback) {
            String cached = semanticCache.getExact(systemPrompt, userInput, model.getModel());
            if (cached != null) {
                log.info("[Gateway] 语义缓存命中 model={}", model.getName());
                return cached;
            }
        }

        // 1. 熔断检查
        if (isCircuitOpen(model.getId())) {
            log.warn("[Gateway] 模型 {} (id={}) 已熔断，触发 fallback", model.getName(), model.getId());
            return executeFallback(model, systemPrompt, userInput, context);
        }

        // 2. 重试调用
        Exception lastException = null;
        for (int attempt = 1; attempt <= DEFAULT_MAX_RETRIES; attempt++) {
            try {
                ChatModel chatModel = getChatModel(model);
                Prompt prompt = buildPrompt(systemPrompt, userInput, context, model);
                ChatResponse response = chatModel.call(prompt);
                // 成功 → 重置失败计数
                resetFailureCounter(model.getId());
                String content = AiUtils.getChatResponseContent(response);
                content = StrUtil.nullToDefault(content, "");
                // 写入语义缓存
                if (!isFallback) {
                    semanticCache.putExact(systemPrompt, userInput, model.getModel(), content);
                }
                return content;
            } catch (Exception e) {
                lastException = e;
                log.warn("[Gateway] 模型 {} (id={}) 第 {}/{} 次调用失败: {}",
                        model.getName(), model.getId(), attempt, DEFAULT_MAX_RETRIES, e.getMessage());
                if (attempt < DEFAULT_MAX_RETRIES) {
                    sleep(calcBackoff(attempt));
                }
            }
        }

        // 3. 全部重试失败
        recordFailure(model.getId());
        log.error("[Gateway] 模型 {} (id={}) 全部 {} 次重试失败，尝试 fallback",
                model.getName(), model.getId(), DEFAULT_MAX_RETRIES, lastException);
        return executeFallback(model, systemPrompt, userInput, context);
    }

    private String executePromptWithRetryAndFallback(AiModelDO model, Prompt prompt) {
        if (isCircuitOpen(model.getId())) {
            log.warn("[Gateway] 模型 {} (id={}) 已熔断，返回兜底回复", model.getName(), model.getId());
            return "【AI服务暂时不可用，请稍后重试】";
        }

        Exception lastException = null;
        for (int attempt = 1; attempt <= DEFAULT_MAX_RETRIES; attempt++) {
            try {
                ChatModel chatModel = getChatModel(model);
                ChatResponse response = chatModel.call(prompt);
                resetFailureCounter(model.getId());
                String content = AiUtils.getChatResponseContent(response);
                return StrUtil.nullToDefault(content, "");
            } catch (Exception e) {
                lastException = e;
                log.warn("[Gateway] 模型 {} (id={}) 第 {}/{} 次调用失败: {}",
                        model.getName(), model.getId(), attempt, DEFAULT_MAX_RETRIES, e.getMessage());
                if (attempt < DEFAULT_MAX_RETRIES) {
                    sleep(calcBackoff(attempt));
                }
            }
        }

        recordFailure(model.getId());
        log.error("[Gateway] 模型 {} (id={}) Prompt调用全部失败", model.getName(), model.getId(), lastException);
        return "【AI服务暂时不可用，请稍后重试】";
    }

    // ========== 流式调用核心 ==========

    private Flux<String> executeStreamWithRetryAndFallback(AiModelDO model, String systemPrompt,
                                                            String userInput, String context) {
        if (isCircuitOpen(model.getId())) {
            log.warn("[Gateway] 模型 {} (id={}) 已熔断，流式返回 fallback", model.getName(), model.getId());
            return Flux.just(executeFallback(model, systemPrompt, userInput, context));
        }

        ChatModel chatModel = getChatModel(model);
        Prompt prompt = buildPrompt(systemPrompt, userInput, context, model);

        return Flux.defer(() -> {
            try {
                return chatModel.stream(prompt)
                        .map(chunk -> {
                            String content = chunk.getResult() != null
                                    ? chunk.getResult().getOutput().getText() : "";
                            return StrUtil.nullToDefault(content, "");
                        })
                        .doOnComplete(() -> resetFailureCounter(model.getId()));
            } catch (Exception e) {
                recordFailure(model.getId());
                return Flux.just("【AI服务暂时不可用，请稍后重试】");
            }
        }).onErrorResume(e -> {
            log.error("[Gateway] 模型 {} (id={}) 流式调用异常", model.getName(), model.getId(), e);
            recordFailure(model.getId());
            return Flux.just("【AI服务暂时不可用，请稍后重试】");
        });
    }

    private Flux<String> executePromptStreamWithRetryAndFallback(AiModelDO model, Prompt prompt) {
        if (isCircuitOpen(model.getId())) {
            return Flux.just("【AI服务暂时不可用，请稍后重试】");
        }

        ChatModel chatModel = getChatModel(model);
        return Flux.defer(() -> {
            try {
                return chatModel.stream(prompt)
                        .map(chunk -> {
                            String content = chunk.getResult() != null
                                    ? chunk.getResult().getOutput().getText() : "";
                            return StrUtil.nullToDefault(content, "");
                        })
                        .doOnComplete(() -> resetFailureCounter(model.getId()));
            } catch (Exception e) {
                recordFailure(model.getId());
                return Flux.just("【AI服务暂时不可用，请稍后重试】");
            }
        }).onErrorResume(e -> {
            log.error("[Gateway] 模型 {} (id={}) Prompt流式调用异常", model.getName(), model.getId(), e);
            recordFailure(model.getId());
            return Flux.just("【AI服务暂时不可用，请稍后重试】");
        });
    }

    // ========== Fallback 链 ==========

    /**
     * 执行 fallback 链：同平台备用模型 → 默认兜底模型 → 静态回复
     */
    private String executeFallback(AiModelDO failedModel, String systemPrompt,
                                    String userInput, String context) {
        // 1. 尝试同类型的其他启用模型
        List<AiModelDO> fallbackModels = modelService.getFallbackModels(
                failedModel.getType(), failedModel.getId());
        for (AiModelDO fallbackModel : fallbackModels) {
            try {
                log.info("[Gateway] 尝试 fallback 模型 {} (id={})", fallbackModel.getName(), fallbackModel.getId());
                ChatModel chatModel = getChatModel(fallbackModel);
                Prompt prompt = buildPrompt(systemPrompt, userInput, context, fallbackModel);
                ChatResponse response = chatModel.call(prompt);
                String content = AiUtils.getChatResponseContent(response);
                return StrUtil.nullToDefault(content, "");
            } catch (Exception e) {
                log.warn("[Gateway] fallback 模型 {} (id={}) 也失败了: {}",
                        fallbackModel.getName(), fallbackModel.getId(), e.getMessage());
            }
        }
        // 2. 最后兜底
        log.error("[Gateway] 所有模型（包括 fallback）均失败，返回兜底回复");
        return "【AI服务暂时不可用，请稍后重试。当前主模型和备用模型均无法响应】";
    }

    // ========== 熔断器逻辑 ==========

    private boolean isCircuitOpen(Long modelId) {
        Long openUntil = circuitOpenTimestamps.get(modelId);
        if (openUntil == null) {
            return false;
        }
        if (System.currentTimeMillis() > openUntil) {
            // 熔断时间已过 → 半开状态
            circuitOpenTimestamps.remove(modelId);
            failureCounters.remove(modelId);
            log.info("[Gateway] 模型 id={} 熔断时间到期，进入半开状态", modelId);
            return false;
        }
        return true;
    }

    private void recordFailure(Long modelId) {
        int failures = failureCounters.computeIfAbsent(modelId, k -> new AtomicInteger(0))
                .incrementAndGet();
        if (failures >= DEFAULT_FAILURE_THRESHOLD) {
            circuitOpenTimestamps.put(modelId,
                    System.currentTimeMillis() + DEFAULT_CIRCUIT_OPEN_MS);
            log.warn("[Gateway] 模型 id={} 连续失败 {} 次，熔断 {} 秒",
                    modelId, failures, DEFAULT_CIRCUIT_OPEN_MS / 1000);
        }
    }

    private void resetFailureCounter(Long modelId) {
        failureCounters.remove(modelId);
        circuitOpenTimestamps.remove(modelId);
    }

    // ========== 辅助方法 ==========

    private ChatModel getChatModel(AiModelDO model) {
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    private Prompt buildPrompt(String systemPrompt, String userInput, String context, AiModelDO model) {
        var messages = new ArrayList<Message>();
        messages.add(new SystemMessage(systemPrompt));
        if (StrUtil.isNotBlank(context)) {
            messages.add(new SystemMessage("历史上下文摘要：" + context));
        }
        messages.add(new UserMessage(userInput));

        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                model.getTemperature(), model.getMaxTokens());
        return new Prompt(messages, options);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private long calcBackoff(int attempt) {
        // 指数退避: 1s, 2s, 4s
        return DEFAULT_RETRY_BACKOFF_MS * (1L << (attempt - 1));
    }

    // ========== 可观测性 ==========

    private void recordSuccessMetrics(AiModelDO model, String platform, long startTime,
                                       ChatResponse response, String traceId, Long userId) {
        int promptTokens = response.getMetadata() != null
                && response.getMetadata().getUsage() != null
                ? response.getMetadata().getUsage().getPromptTokens() : 0;
        int completionTokens = response.getMetadata() != null
                && response.getMetadata().getUsage() != null
                ? response.getMetadata().getUsage().getCompletionTokens() : 0;
        long latencyMs = System.currentTimeMillis() - startTime;
        double cost = AiModelPricing.estimateCost(model.getModel(), promptTokens, completionTokens);

        // Micrometer 指标
        callMetrics.recordCall(platform, model.getModel(), "chat", true,
                latencyMs, promptTokens, completionTokens, cost);

        // 数据库详情
        AiApiCallDetailDO detail = AiApiCallDetailDO.builder()
                .userId(userId).modelId(model.getId()).modelName(model.getName())
                .platform(platform).apiType("chat")
                .promptTokens(promptTokens).completionTokens(completionTokens)
                .totalTokens(promptTokens + completionTokens)
                .latencyMs((int) latencyMs).estimatedCost(BigDecimal.valueOf(cost))
                .success(true).traceId(traceId)
                .createTime(LocalDateTime.now())
                .build();
        callDetailService.createCallDetail(detail);
    }

    private void recordFailureMetrics(AiModelDO model, String platform, long startTime,
                                       String errorMsg, String traceId, Long userId) {
        long latencyMs = System.currentTimeMillis() - startTime;
        callMetrics.recordCall(platform, model.getModel(), "chat", false,
                latencyMs, 0, 0, 0);

        AiApiCallDetailDO detail = AiApiCallDetailDO.builder()
                .userId(userId).modelId(model.getId()).modelName(model.getName())
                .platform(platform).apiType("chat")
                .latencyMs((int) latencyMs).estimatedCost(BigDecimal.ZERO)
                .success(false).errorMessage(errorMsg).traceId(traceId)
                .createTime(LocalDateTime.now())
                .build();
        callDetailService.createCallDetail(detail);
    }
}
