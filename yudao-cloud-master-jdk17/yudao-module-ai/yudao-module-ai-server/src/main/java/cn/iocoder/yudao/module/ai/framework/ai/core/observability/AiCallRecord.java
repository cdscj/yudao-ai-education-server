package cn.iocoder.yudao.module.ai.framework.ai.core.observability;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 单次 AI 调用记录 — 包含完整的调用元数据
 *
 * @author yudao
 */
@Data
public class AiCallRecord {

    /** 链路追踪 ID */
    private String traceId;

    /** 用户 ID */
    private Long userId;

    /** 模型 ID */
    private Long modelId;

    /** 模型名称 */
    private String modelName;

    /** 平台 */
    private String platform;

    /** API 类型 (chat/embedding/image/music) */
    private String apiType;

    /** Prompt 内容（截断至 500 字符） */
    private String promptSnippet;

    /** 响应内容（截断至 500 字符） */
    private String completionSnippet;

    /** Prompt tokens */
    private int promptTokens;

    /** Completion tokens */
    private int completionTokens;

    /** 总 tokens */
    private int totalTokens;

    /** 延迟（毫秒） */
    private long latencyMs;

    /** 估算费用（美元） */
    private double estimatedCost;

    /** 是否成功 */
    private boolean success;

    /** 错误信息 */
    private String errorMessage;

    /** 对话 ID */
    private Long conversationId;

    /** 创建时间 */
    private LocalDateTime createTime;

    public static AiCallRecord create(Long userId, Long modelId, String modelName,
                                       String platform, String apiType) {
        AiCallRecord record = new AiCallRecord();
        record.setTraceId(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        record.setUserId(userId);
        record.setModelId(modelId);
        record.setModelName(modelName);
        record.setPlatform(platform);
        record.setApiType(apiType);
        record.setCreateTime(LocalDateTime.now());
        return record;
    }
}
