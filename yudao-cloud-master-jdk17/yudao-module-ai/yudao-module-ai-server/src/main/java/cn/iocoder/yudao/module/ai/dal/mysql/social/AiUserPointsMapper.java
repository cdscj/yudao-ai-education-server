package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserPointsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface AiUserPointsMapper extends BaseMapperX<AiUserPointsDO> {

    default AiUserPointsDO selectByUserId(Long userId) {
        return selectOne(AiUserPointsDO::getUserId, userId);
    }

    default List<AiUserPointsDO> selectListByUserId(Long userId, Integer pageNo, Integer pageSize) {
        return selectList(new LambdaQueryWrapperX<AiUserPointsDO>()
                .eq(AiUserPointsDO::getUserId, userId)
                .orderByDesc(AiUserPointsDO::getCreateTime)
                .last("LIMIT " + (pageNo - 1) * pageSize + "," + pageSize));
    }

    @Update("UPDATE ai_user_points SET weekly_points = 0")
    int resetWeeklyPoints();

}
