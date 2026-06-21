package cn.iocoder.yudao.module.ai.framework.ai.core.observability;

/**
 * AI 调用链路追踪上下文 — 基于 ThreadLocal 在请求线程内传递 trace_id
 *
 * <p>典型用法：
 * <pre>{@code
 * String traceId = AiTraceContext.start("chat", userId);
 * try {
 *     // 多次 LLM 调用共享同一 trace_id
 *     gateway.chatSync(modelId, prompt);
 * } finally {
 *     AiTraceContext.clear();
 * }
 * }</pre>
 * </p>
 *
 * @author yudao
 */
public class AiTraceContext {

    private static final ThreadLocal<TraceInfo> CONTEXT = new ThreadLocal<>();

    private AiTraceContext() {}

    /**
     * 开始一次追踪
     *
     * @param apiType API类型
     * @param userId  用户ID
     * @return trace_id
     */
    public static String start(String apiType, Long userId) {
        TraceInfo info = new TraceInfo();
        info.setApiType(apiType);
        info.setUserId(userId);
        info.setStartTime(System.currentTimeMillis());
        CONTEXT.set(info);
        return info.getTraceId();
    }

    /**
     * 获取当前上下文
     */
    public static TraceInfo get() {
        return CONTEXT.get();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * 获取当前 trace_id
     */
    public static String getTraceId() {
        TraceInfo info = CONTEXT.get();
        return info != null ? info.getTraceId() : null;
    }

    /**
     * 追踪信息
     */
    public static class TraceInfo {
        private final String traceId = java.util.UUID.randomUUID().toString()
                .replace("-", "").substring(0, 16);
        private String apiType;
        private Long userId;
        private long startTime;

        public String getTraceId() { return traceId; }
        public String getApiType() { return apiType; }
        public void setApiType(String apiType) { this.apiType = apiType; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getElapsedMs() { return System.currentTimeMillis() - startTime; }
    }
}
