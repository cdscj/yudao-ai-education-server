package cn.iocoder.yudao.module.ai.framework.ai.core.gateway;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 模型网关配置
 *
 * @author yudao
 */
@ConfigurationProperties(prefix = "yudao.ai.gateway")
@Data
public class AiModelRouteConfig {

    /**
     * 重试配置
     */
    private Retry retry = new Retry();

    /**
     * 熔断器配置
     */
    private CircuitBreaker circuitBreaker = new CircuitBreaker();

    /**
     * Fallback 配置
     */
    private Fallback fallback = new Fallback();

    @Data
    public static class Retry {
        private int maxAttempts = 3;
        private long backoffDelay = 1000; // 毫秒
    }

    @Data
    public static class CircuitBreaker {
        private int failureThreshold = 5;
        private long openTimeout = 30_000; // 毫秒
    }

    @Data
    public static class Fallback {
        private boolean enabled = true;
        private Long defaultModelId; // 兜底模型ID
    }
}
