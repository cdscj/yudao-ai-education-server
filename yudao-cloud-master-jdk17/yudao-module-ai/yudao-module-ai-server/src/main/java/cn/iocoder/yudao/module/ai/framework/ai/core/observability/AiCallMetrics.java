package cn.iocoder.yudao.module.ai.framework.ai.core.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * AI 调用指标收集器 — 基于 Micrometer 注册到 Prometheus
 *
 * <p>提供以下指标：
 * <ul>
 *   <li>{@code ai.calls.total} — Counter, 标签: platform, model, api_type, success</li>
 *   <li>{@code ai.calls.latency} — Timer, 标签: platform, model, api_type</li>
 *   <li>{@code ai.tokens.prompt} — Counter, 标签: platform, model</li>
 *   <li>{@code ai.tokens.completion} — Counter, 标签: platform, model</li>
 *   <li>{@code ai.cost.total} — Counter, 标签: platform, model</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AiCallMetrics {

    @Getter
    private final MeterRegistry meterRegistry;

    private final Map<String, Counter> callCounters = new ConcurrentHashMap<>();
    private final Map<String, Timer> latencyTimers = new ConcurrentHashMap<>();
    private final Map<String, Counter> tokenPromptCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> tokenCompletionCounters = new ConcurrentHashMap<>();
    private final Map<String, Counter> costCounters = new ConcurrentHashMap<>();

    public AiCallMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    /**
     * 记录一次 API 调用
     */
    public void recordCall(String platform, String model, String apiType,
                           boolean success, long latencyMs,
                           int promptTokens, int completionTokens, double estimatedCost) {
        try {
            // 调用计数
            counter("ai.calls.total", "platform", platform, "model", model,
                    "api_type", apiType, "success", String.valueOf(success))
                    .increment();

            // 延迟
            timer("ai.calls.latency", "platform", platform, "model", model, "api_type", apiType)
                    .record(latencyMs, TimeUnit.MILLISECONDS);

            // Token 用量
            if (promptTokens > 0) {
                counter("ai.tokens.prompt", "platform", platform, "model", model)
                        .increment(promptTokens);
            }
            if (completionTokens > 0) {
                counter("ai.tokens.completion", "platform", platform, "model", model)
                        .increment(completionTokens);
            }

            // 费用
            if (estimatedCost > 0) {
                counter("ai.cost.total", "platform", platform, "model", model)
                        .increment((long) (estimatedCost * 1_000_000)); // 存为微分为整数
            }
        } catch (Exception e) {
            // 指标记录失败不应影响主流程
            log.debug("[AiCallMetrics] 记录指标失败: {}", e.getMessage());
        }
    }

    private Counter counter(String name, String... tags) {
        String key = name + ":" + String.join(",", tags);
        return callCounters.computeIfAbsent(key, k ->
                Counter.builder(name)
                        .tags(tags)
                        .description("AI call metrics - " + name)
                        .register(meterRegistry));
    }

    private Timer timer(String name, String... tags) {
        String key = name + ":" + String.join(",", tags);
        return latencyTimers.computeIfAbsent(key, k ->
                Timer.builder(name)
                        .tags(tags)
                        .description("AI call metrics - " + name)
                        .register(meterRegistry));
    }
}
