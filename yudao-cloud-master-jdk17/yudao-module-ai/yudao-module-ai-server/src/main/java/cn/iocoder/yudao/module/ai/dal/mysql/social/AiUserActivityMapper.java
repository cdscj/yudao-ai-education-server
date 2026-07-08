package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserActivityDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AiUserActivityMapper extends BaseMapperX<AiUserActivityDO> {

    default List<AiUserActivityDO> selectListByUserId(Long userId, int limit) {
        return selectList(new LambdaQueryWrapperX<AiUserActivityDO>()
                .eq(AiUserActivityDO::getUserId, userId)
                .orderByDesc(AiUserActivityDO::getId)
                .last("LIMIT " + limit));
    }

    default List<AiUserActivityDO> selectListByUserIds(@Param("userIds") List<Long> userIds, @Param("limit") Integer limit) {
        if (userIds == null || userIds.isEmpty()) return List.of();
        return selectList(new LambdaQueryWrapperX<AiUserActivityDO>()
                .in(AiUserActivityDO::getUserId, userIds)
                .orderByDesc(AiUserActivityDO::getCreateTime)
                .last("LIMIT " + limit));
    }

}
