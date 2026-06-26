package cn.iocoder.yudao.module.ai.dal.mysql.agent;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentMessageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI Agent 消息 Mapper
 *
 * @author yudao
 */
@Mapper
public interface AiAgentMessageMapper extends BaseMapperX<AiAgentMessageDO> {

    /**
     * 查询会话的所有消息
     *
     * @param conversationId 会话编号
     * @return 消息列表，按创建时间升序
     */
    default List<AiAgentMessageDO> selectListByConversationId(Long conversationId) {
        return selectList(new LambdaQueryWrapperX<AiAgentMessageDO>()
                .eq(AiAgentMessageDO::getConversationId, conversationId)
                .orderByAsc(AiAgentMessageDO::getCreateTime));
    }

    /**
     * 获取会话的消息数量
     *
     * @param conversationId 会话编号
     * @return 消息数量
     */
    default Long selectCountByConversationId(Long conversationId) {
        return selectCount(new LambdaQueryWrapperX<AiAgentMessageDO>()
                .eq(AiAgentMessageDO::getConversationId, conversationId));
    }
}
