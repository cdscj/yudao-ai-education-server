package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.EvaluationPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningEvaluationDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiLearningEvaluationMapper extends BaseMapperX<AiLearningEvaluationDO> {

    default List<AiLearningEvaluationDO> selectListByUserId(Long userId) {
        return selectList(AiLearningEvaluationDO::getUserId, userId);
    }

    default List<AiLearningEvaluationDO> selectListByProfileId(Long profileId) {
        return selectList(AiLearningEvaluationDO::getProfileId, profileId);
    }

    default PageResult<AiLearningEvaluationDO> selectPage(EvaluationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiLearningEvaluationDO>()
                .eqIfPresent(AiLearningEvaluationDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiLearningEvaluationDO::getEvaluationType, reqVO.getEvaluationType())
                .orderByDesc(AiLearningEvaluationDO::getId));
    }
}
