package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserActivityDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AiUserActivityMapper extends BaseMapperX<AiUserActivityDO> {

    default List<AiUserActivityDO> selectListByUserId(Long userId, int limit) {
        return selectList(new LambdaQueryWrapperX<AiUserActivityDO>()
                .eq(AiUserActivityDO::getUserId, userId)
                .orderByDesc(AiUserActivityDO::getId)
                .last("LIMIT " + limit));
    }

    @Select("SELECT * FROM ai_user_activity WHERE user_id IN (" +
            "<script>" +
            "<foreach collection='userIds' item='id' separator=','>#{id}</foreach>" +
            "</script>" +
            ") AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<AiUserActivityDO> selectListByUserIds(@Param("userIds") List<Long> userIds, @Param("limit") Integer limit);

}
