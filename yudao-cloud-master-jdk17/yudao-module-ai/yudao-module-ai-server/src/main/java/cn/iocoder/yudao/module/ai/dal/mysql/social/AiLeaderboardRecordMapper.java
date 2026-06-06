package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLeaderboardRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AiLeaderboardRecordMapper extends BaseMapperX<AiLeaderboardRecordDO> {

    default PageResult<AiLeaderboardRecordDO> selectPage(Long userId, String periodType,
                                                         LocalDate snapshotDate, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiLeaderboardRecordDO>()
                .eqIfPresent(AiLeaderboardRecordDO::getUserId, userId)
                .eqIfPresent(AiLeaderboardRecordDO::getPeriodType, periodType)
                .eqIfPresent(AiLeaderboardRecordDO::getSnapshotDate, snapshotDate)
                .orderByDesc(AiLeaderboardRecordDO::getId));
    }

    default List<AiLeaderboardRecordDO> selectListByPeriodAndDate(String periodType, LocalDate date) {
        return selectList(new LambdaQueryWrapperX<AiLeaderboardRecordDO>()
                .eq(AiLeaderboardRecordDO::getPeriodType, periodType)
                .eq(AiLeaderboardRecordDO::getSnapshotDate, date)
                .orderByAsc(AiLeaderboardRecordDO::getRank));
    }

}
