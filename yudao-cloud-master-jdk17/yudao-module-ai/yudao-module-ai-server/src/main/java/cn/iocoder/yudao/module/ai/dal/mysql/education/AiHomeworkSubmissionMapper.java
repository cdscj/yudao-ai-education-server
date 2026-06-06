package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkSubmissionDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiHomeworkSubmissionMapper extends BaseMapperX<AiHomeworkSubmissionDO> {

    default PageResult<AiHomeworkSubmissionDO> selectPage(PageParam pageParam, Long homeworkId, Long userId, String gradeStatus) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiHomeworkSubmissionDO>()
                .eqIfPresent(AiHomeworkSubmissionDO::getHomeworkId, homeworkId)
                .eqIfPresent(AiHomeworkSubmissionDO::getUserId, userId)
                .eqIfPresent(AiHomeworkSubmissionDO::getGradeStatus, gradeStatus)
                .orderByDesc(AiHomeworkSubmissionDO::getId));
    }

    default AiHomeworkSubmissionDO selectByHomeworkAndUser(Long homeworkId, Long userId) {
        return selectOne(new LambdaQueryWrapperX<AiHomeworkSubmissionDO>()
                .eq(AiHomeworkSubmissionDO::getHomeworkId, homeworkId)
                .eq(AiHomeworkSubmissionDO::getUserId, userId));
    }

    default long selectCountByHomeworkId(Long homeworkId) {
        return selectCount(AiHomeworkSubmissionDO::getHomeworkId, homeworkId);
    }

    default long selectCountByUserId(Long userId) {
        return selectCount(AiHomeworkSubmissionDO::getUserId, userId);
    }
}
