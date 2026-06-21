package cn.iocoder.yudao.module.ai.dal.dataobject.config;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI API 调用详情 DO — 单次 LLM 调用的完整可观测数据
 *
 * <p>区别于 {@link AiApiStatisticsDO}（按日期聚合的统计），本表记录每次调用的明细，
 * 用于 Token 用量分析、延迟分布、成本核算、问题排查。
 * </p>
 *
 * @author yudao
 */
@TableName("ai_api_call_detail")
@KeySequence("ai_api_call_detail_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class AiApiCallDetailDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 用户编号
     */
    private Long userId;

    /**
     * 模型编号
     */
    private Long modelId;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 平台
     */
    private String platform;

    /**
     * API 类型 (chat/embedding/image/music)
     */
    private String apiType;

    /**
     * Prompt 内容摘要（截断至 500 字符）
     */
    private String promptSnippet;

    /**
     * 响应内容摘要（截断至 500 字符）
     */
    private String completionSnippet;

    /**
     * Prompt Token 数
     */
    private Integer promptTokens;

    /**
     * Completion Token 数
     */
    private Integer completionTokens;

    /**
     * 总 Token 数
     */
    private Integer totalTokens;

    /**
     * 延迟（毫秒）
     */
    private Integer latencyMs;

    /**
     * 估算费用（美元）
     */
    private BigDecimal estimatedCost;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 链路追踪 ID
     */
    private String traceId;

    /**
     * 对话 ID
     */
    private Long conversationId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

}
