package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface AiQuestionBankMapper extends BaseMapperX<AiQuestionBankDO> {

    default PageResult<AiQuestionBankDO> selectPage(PageParam pageParam, Long subjectId, String questionType,
                                                     Integer difficulty, String keyword, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiQuestionBankDO>()
                .eqIfPresent(AiQuestionBankDO::getSubjectId, subjectId)
                .eqIfPresent(AiQuestionBankDO::getQuestionType, questionType)
                .eqIfPresent(AiQuestionBankDO::getDifficulty, difficulty)
                .eqIfPresent(AiQuestionBankDO::getStatus, status)
                .and(keyword != null, w -> w.like(AiQuestionBankDO::getTitle, keyword)
                        .or().like(AiQuestionBankDO::getContent, keyword))
                .orderByAsc(AiQuestionBankDO::getSort)
                .orderByDesc(AiQuestionBankDO::getId));
    }

    default List<AiQuestionBankDO> selectListByIds(Collection<Long> ids) {
        return selectBatchIds(ids);
    }

    default long selectCountBySubjectId(Long subjectId) {
        return selectCount(AiQuestionBankDO::getSubjectId, subjectId);
    }
}
