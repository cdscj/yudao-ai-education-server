package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamRecordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiExamRecordMapper extends BaseMapperX<AiExamRecordDO> {

    default PageResult<AiExamRecordDO> selectPage(PageParam pageParam, Long examId, Long userId, String status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiExamRecordDO>()
                .eqIfPresent(AiExamRecordDO::getExamId, examId)
                .eqIfPresent(AiExamRecordDO::getUserId, userId)
                .eqIfPresent(AiExamRecordDO::getStatus, status)
                .orderByDesc(AiExamRecordDO::getId));
    }

    default List<AiExamRecordDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiExamRecordDO>()
                .eq(AiExamRecordDO::getUserId, userId)
                .orderByDesc(AiExamRecordDO::getId));
    }

    default long selectCountByExamId(Long examId) {
        return selectCount(AiExamRecordDO::getExamId, examId);
    }

    default long selectCountByUserId(Long userId) {
        return selectCount(AiExamRecordDO::getUserId, userId);
    }
}
