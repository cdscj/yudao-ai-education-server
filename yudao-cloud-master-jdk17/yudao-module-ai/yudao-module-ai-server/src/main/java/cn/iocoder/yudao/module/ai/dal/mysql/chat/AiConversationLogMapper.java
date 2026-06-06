package cn.iocoder.yudao.module.ai.dal.mysql.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.log.AiConversationLogPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiConversationLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * AI 对话日志 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiConversationLogMapper extends BaseMapperX<AiConversationLogDO> {

    default PageResult<AiConversationLogDO> selectPage(AiConversationLogPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiConversationLogDO>()
                .eqIfPresent(AiConversationLogDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiConversationLogDO::getConversationId, reqVO.getConversationId())
                .eqIfPresent(AiConversationLogDO::getSuccess, reqVO.getSuccess())
                .betweenIfPresent(AiConversationLogDO::getCreateTime, reqVO.getCreateTimeStart(), reqVO.getCreateTimeEnd())
                .orderByDesc(AiConversationLogDO::getId));
    }

    /**
     * 统计指定日期范围内的对话日志数量
     */
    @Select("SELECT COUNT(*) FROM ai_conversation_log " +
            "WHERE create_time >= #{startTime} AND create_time < #{endTime}")
    Long selectCountByDateRange(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

}
