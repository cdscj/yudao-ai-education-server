package cn.iocoder.yudao.module.ai.dal.dataobject.agent;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI Agent 会话 DO
 *
 * <p>记录用户与 Agent 的一次完整对话会话，一个会话可包含多条消息。
 * 类似于 {@link cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO}，
 * 但专用于 Agent 场景，增加了 Agent 和模型关联。</p>
 *
 * @author yudao
 */
@TableName("ai_agent_conversation")
@KeySequence("ai_agent_conversation_seq")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiAgentConversationDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     *
     * 关联 {@link cn.iocoder.yudao.module.system.dal.dataobject.user.AdminUserDO#getId()}
     */
    private Long userId;
    /**
     * Agent 配置编号
     *
     * 关联 {@link AiAgentConfigDO#getId()}
     */
    private Long agentId;
    /**
     * 会话标题
     */
    private String title;
    /**
     * 使用的 AI 模型编号
     *
     * 关联 {@link cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO#getId()}
     */
    private Long modelId;
    /**
     * 会话状态
     *
     * true - 进行中，false - 已结束
     */
    private Boolean active;
    /**
     * 消息数量
     */
    private Integer messageCount;
    /**
     * 使用的 Token 总数
     */
    private Integer totalTokens;

}
