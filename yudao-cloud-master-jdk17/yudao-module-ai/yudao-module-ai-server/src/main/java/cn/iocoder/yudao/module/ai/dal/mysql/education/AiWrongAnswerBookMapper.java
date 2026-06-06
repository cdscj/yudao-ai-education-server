package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiWrongAnswerBookMapper extends BaseMapperX<AiWrongAnswerBookDO> {

    default PageResult<AiWrongAnswerBookDO> selectPage(PageParam pageParam, Long userId, Long subjectId,
                                                        Integer masteryLevel) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiWrongAnswerBookDO>()
                .eqIfPresent(AiWrongAnswerBookDO::getUserId, userId)
                .eqIfPresent(AiWrongAnswerBookDO::getSubjectId, subjectId)
                .eqIfPresent(AiWrongAnswerBookDO::getMasteryLevel, masteryLevel)
                .orderByDesc(AiWrongAnswerBookDO::getId));
    }

    default AiWrongAnswerBookDO selectByUserIdAndQuestionId(Long userId, Long questionId) {
        return selectOne(new LambdaQueryWrapperX<AiWrongAnswerBookDO>()
                .eq(AiWrongAnswerBookDO::getUserId, userId)
                .eq(AiWrongAnswerBookDO::getQuestionId, questionId));
    }

    default long selectCountByUserId(Long userId) {
        return selectCount(AiWrongAnswerBookDO::getUserId, userId);
    }

    default long selectCountByUserIdAndSubjectId(Long userId, Long subjectId) {
        return selectCount(new LambdaQueryWrapperX<AiWrongAnswerBookDO>()
                .eq(AiWrongAnswerBookDO::getUserId, userId)
                .eq(AiWrongAnswerBookDO::getSubjectId, subjectId));
    }
}
