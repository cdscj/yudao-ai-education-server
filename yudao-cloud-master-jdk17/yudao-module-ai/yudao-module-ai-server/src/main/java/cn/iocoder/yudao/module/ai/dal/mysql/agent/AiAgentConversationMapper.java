package cn.iocoder.yudao.module.ai.dal.mysql.agent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConversationPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConversationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI Agent 会话 Mapper
 *
 * @author yudao
 */
@Mapper
public interface AiAgentConversationMapper extends BaseMapperX<AiAgentConversationDO> {

    /**
     * 查询用户的所有活跃会话
     *
     * @param userId 用户编号
     * @return 会话列表
     */
    default List<AiAgentConversationDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiAgentConversationDO>()
                .eq(AiAgentConversationDO::getUserId, userId)
                .eq(AiAgentConversationDO::getActive, true)
                .orderByDesc(AiAgentConversationDO::getUpdateTime));
    }

    /**
     * 分页查询 Agent 会话
     */
    default PageResult<AiAgentConversationDO> selectPage(AiAgentConversationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiAgentConversationDO>()
                .eqIfPresent(AiAgentConversationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiAgentConversationDO::getAgentId, reqVO.getAgentId())
                .likeIfPresent(AiAgentConversationDO::getTitle, reqVO.getTitle())
                .eqIfPresent(AiAgentConversationDO::getActive, reqVO.getActive())
                .orderByDesc(AiAgentConversationDO::getCreateTime));
    }
}
