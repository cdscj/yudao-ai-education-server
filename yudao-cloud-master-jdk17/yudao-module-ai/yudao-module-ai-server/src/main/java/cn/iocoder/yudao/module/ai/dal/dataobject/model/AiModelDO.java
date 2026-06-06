package cn.iocoder.yudao.module.ai.dal.dataobject.model;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI 模型 DO
 *
 * 默认模型：{@link #status} 为开启，并且 {@link #sort} 排序第一
 *
 * @author fansili
 * @since 2024/4/24 19:39
 */
@TableName("ai_model")
@KeySequence("ai_model_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class AiModelDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * API 秘钥编号
     *
     * 关联 {@link AiApiKeyDO#getId()}
     */
    private Long keyId;
    /**
     * 模型名称
     */
    private String name;
    /**
     * 模型标志
     */
    private String model;
    /**
     * 平台
     *
     * 枚举 {@link AiPlatformEnum}
     */
    private String platform;
    /**
     * 类型
     *
     * 枚举 {@link AiModelTypeEnum}
     */
    private Integer type;

    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

    // ========== 对话配置 ==========

    /**
     * 温度参数
     *
     * 用于调整生成回复的随机性和多样性程度：较低的温度值会使输出更收敛于高频词汇，较高的则增加多样性
     */
    private Double temperature;
    /**
     * 单条回复的最大 Token 数量
     */
    private Integer maxTokens;
    /**
     * 上下文的最大 Message 数量
     */
    private Integer maxContexts;
    /**
     * TopP 核采样参数
     *
     * 值范围 0.0-1.0，越接近 1.0 生成的文本随机性越高
     */
    private Double topP;
    /**
     * 频率惩罚
     *
     * 值范围 -2.0 到 2.0，正值降低重复内容可能性
     */
    private Double frequencyPenalty;
    /**
     * 存在惩罚
     *
     * 值范围 -2.0 到 2.0，正值降低话题重复可能性
     */
    private Double presencePenalty;

}
