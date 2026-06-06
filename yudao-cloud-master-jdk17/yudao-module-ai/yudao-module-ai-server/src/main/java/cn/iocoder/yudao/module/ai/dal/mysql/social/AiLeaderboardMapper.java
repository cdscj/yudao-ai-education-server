package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLeaderboardDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiLeaderboardMapper extends BaseMapperX<AiLeaderboardDO> {

    default List<AiLeaderboardDO> selectListByPeriod(String periodType, String periodValue) {
        return selectList(new LambdaQueryWrapperX<AiLeaderboardDO>()
                .eq(AiLeaderboardDO::getPeriodType, periodType)
                .eq(AiLeaderboardDO::getPeriodValue, periodValue)
                .orderByAsc(AiLeaderboardDO::getRank));
    }

    default AiLeaderboardDO selectByUserIdAndPeriod(Long userId, String periodType, String periodValue) {
        return selectOne(new LambdaQueryWrapperX<AiLeaderboardDO>()
                .eq(AiLeaderboardDO::getUserId, userId)
                .eq(AiLeaderboardDO::getPeriodType, periodType)
                .eq(AiLeaderboardDO::getPeriodValue, periodValue));
    }

    default void deleteByPeriod(String periodType, String periodValue) {
        delete(new LambdaQueryWrapperX<AiLeaderboardDO>()
                .eq(AiLeaderboardDO::getPeriodType, periodType)
                .eq(AiLeaderboardDO::getPeriodValue, periodValue));
    }
}
