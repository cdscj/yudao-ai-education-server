package cn.iocoder.yudao.module.ai.service.agent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConversationPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentChatSendReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConversationDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentMessageDO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * AI Agent 会话 Service 接口
 *
 * @author yudao
 */
public interface AiAgentConversationService {

    /**
     * 创建会话并发送第一条消息（阻塞式）
     *
     * @param userId   用户编号
     * @param sendReqVO 发送请求
     * @return Agent 回复消息
     */
    AiAgentMessageDO createConversationAndSend(Long userId, AiAgentChatSendReqVO sendReqVO);

    /**
     * 向已有会话发送消息（阻塞式）
     *
     * @param userId   用户编号
     * @param sendReqVO 发送请求
     * @return Agent 回复消息
     */
    AiAgentMessageDO sendMessage(Long userId, AiAgentChatSendReqVO sendReqVO);

    /**
     * 向会话发送消息（流式 SSE）
     *
     * @param userId   用户编号
     * @param sendReqVO 发送请求
     * @return 消息事件流，每个事件包含 [角色前缀]+内容
     */
    Flux<String> sendMessageStream(Long userId, AiAgentChatSendReqVO sendReqVO);

    /**
     * 获得 Agent 会话分页
     *
     * @param pageReqVO 分页查询
     * @return 会话分页
     */
    PageResult<AiAgentConversationDO> getConversationPage(AiAgentConversationPageReqVO pageReqVO);

    /**
     * 获取用户的活跃会话列表
     *
     * @param userId 用户编号
     * @return 会话列表
     */
    List<AiAgentConversationDO> getConversationListByUserId(Long userId);

    /**
     * 获得会话详情
     *
     * @param id 会话编号
     * @return 会话
     */
    AiAgentConversationDO getConversation(Long id);

    /**
     * 删除（结束）会话
     *
     * @param id 会话编号
     */
    void deleteConversation(Long id);
}
