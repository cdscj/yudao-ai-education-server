package cn.iocoder.yudao.module.ai.service.agent;

import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentMessageDO;

import java.util.List;

/**
 * AI Agent 消息 Service 接口
 *
 * @author yudao
 */
public interface AiAgentMessageService {

    /**
     * 创建用户消息
     *
     * @param conversationId 会话编号
     * @param content        消息内容
     * @return 消息对象
     */
    AiAgentMessageDO createUserMessage(Long conversationId, String content);

    /**
     * 创建 Assistant 消息
     *
     * @param conversationId 会话编号
     * @param content        回复内容
     * @param reasoning      推理过程（可为 null）
     * @param usageTokens    Token 用量（可为 null）
     * @return 消息对象
     */
    AiAgentMessageDO createAssistantMessage(Long conversationId, String content,
                                            String reasoning, Integer usageTokens);

    /**
     * 创建错误消息
     *
     * @param conversationId 会话编号
     * @param errorMsg       错误描述
     * @return 消息对象
     */
    AiAgentMessageDO createErrorMessage(Long conversationId, String errorMsg);

    /**
     * 获取会话的消息列表
     *
     * @param conversationId 会话编号
     * @return 消息列表，按时间升序
     */
    List<AiAgentMessageDO> getMessageList(Long conversationId);

    /**
     * 构建会话上下文（历史消息拼接）
     *
     * @param conversationId 会话编号
     * @return 格式化的上下文文本
     */
    String buildContext(Long conversationId);
}
