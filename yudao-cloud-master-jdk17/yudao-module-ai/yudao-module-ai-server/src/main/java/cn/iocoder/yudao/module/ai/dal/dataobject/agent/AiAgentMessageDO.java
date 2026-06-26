package cn.iocoder.yudao.module.ai.dal.dataobject.agent;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI Agent 消息 DO
 *
 * <p>记录 Agent 会话中的每条消息，包括用户输入和 Agent 响应。
 * 支持错误标记和 Token 用量追踪。</p>
 *
 * @author yudao
 */
@TableName("ai_agent_message")
@KeySequence("ai_agent_message_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentMessageDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 会话编号
     *
     * 关联 {@link AiAgentConversationDO#getId()}
     */
    private Long conversationId;
    /**
     * 消息角色
     *
     * user - 用户消息 | assistant - Agent 回复
     */
    private String role;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 是否为错误消息
     */
    private Boolean isError;
    /**
     * 本次消息消耗的 Token 数
     */
    private Integer usageTokens;
    /**
     * Agent 的推理过程（ReAct 模式的中间思考步骤，JSON 格式）
     */
    private String reasoning;

}
