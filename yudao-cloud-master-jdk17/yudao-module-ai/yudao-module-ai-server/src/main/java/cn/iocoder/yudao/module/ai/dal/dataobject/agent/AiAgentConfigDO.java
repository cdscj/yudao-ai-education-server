package cn.iocoder.yudao.module.ai.dal.dataobject.agent;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.ai.enums.agent.AiAgentTypeEnum;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI Agent 配置 DO
 *
 * <p>存储每个 Agent 实例的配置信息，包括：
 * <ul>
 *   <li>基本信息：名称、类型、描述</li>
 *   <li>模型配置：关联的 AI 模型、温度、最大 Token 数</li>
 *   <li>行为配置：系统提示词、最大推理步数</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
@TableName("ai_agent_config")
@KeySequence("ai_agent_config_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentConfigDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * Agent 名称（展示用）
     */
    private String name;
    /**
     * Agent 类型
     *
     * 枚举 {@link AiAgentTypeEnum}
     */
    private Integer type;
    /**
     * 关联的 AI 模型编号
     *
     * 关联 {@link cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO#getId()}
     */
    private Long modelId;
    /**
     * 系统提示词
     *
     * 用于定义 Agent 的行为和角色，可覆盖 Agent 默认的系统提示词
     */
    private String systemPrompt;
    /**
     * Agent 描述
     */
    private String description;
    /**
     * 最大推理步数（ReAct 模式）
     *
     * 仅在 ReAct 类型的 Agent 生效，默认 10
     */
    private Integer maxSteps;
    /**
     * 温度参数（0.0-2.0），控制回复的随机性
     */
    private Double temperature;
    /**
     * 单次回复最大 Token 数
     */
    private Integer maxTokens;
    /**
     * 排序值（越小越靠前）
     */
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
