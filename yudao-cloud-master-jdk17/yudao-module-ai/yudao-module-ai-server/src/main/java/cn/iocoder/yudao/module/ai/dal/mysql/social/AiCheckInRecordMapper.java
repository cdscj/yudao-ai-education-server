package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiCheckInRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AiCheckInRecordMapper extends BaseMapperX<AiCheckInRecordDO> {

    default AiCheckInRecordDO selectByUserIdAndDate(Long userId, LocalDate date) {
        return selectOne(AiCheckInRecordDO::getUserId, userId,
                AiCheckInRecordDO::getCheckInDate, date);
    }

    default AiCheckInRecordDO selectLastByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<AiCheckInRecordDO>()
                .eq(AiCheckInRecordDO::getUserId, userId)
                .orderByDesc(AiCheckInRecordDO::getCheckInDate)
                .last("LIMIT 1"));
    }

    default Long selectCountByUserId(Long userId) {
        return selectCount(AiCheckInRecordDO::getUserId, userId);
    }

    default List<AiCheckInRecordDO> selectListByUserId(Long userId, Integer pageNo, Integer pageSize) {
        return selectList(new LambdaQueryWrapperX<AiCheckInRecordDO>()
                .eq(AiCheckInRecordDO::getUserId, userId)
                .orderByDesc(AiCheckInRecordDO::getCheckInDate)
                .last("LIMIT " + (pageNo - 1) * pageSize + "," + pageSize));
    }

    default List<AiCheckInRecordDO> selectListByUserIdAndMonth(Long userId, String yearMonth) {
        return selectList(new LambdaQueryWrapperX<AiCheckInRecordDO>()
                .eq(AiCheckInRecordDO::getUserId, userId)
                .apply("DATE_FORMAT(check_in_date, '%Y-%m') = {0}", yearMonth)
                .orderByAsc(AiCheckInRecordDO::getCheckInDate));
    }
}
