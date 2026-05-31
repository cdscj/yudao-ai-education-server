package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourcePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningResourceDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiLearningResourceMapper extends BaseMapperX<AiLearningResourceDO> {

    default List<AiLearningResourceDO> selectListByUserId(Long userId) {
        return selectList(AiLearningResourceDO::getUserId, userId);
    }

    default List<AiLearningResourceDO> selectListByProfileId(Long profileId) {
        return selectList(AiLearningResourceDO::getProfileId, profileId);
    }

    default PageResult<AiLearningResourceDO> selectPage(LearningResourcePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiLearningResourceDO>()
                .eqIfPresent(AiLearningResourceDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiLearningResourceDO::getResourceType, reqVO.getResourceType())
                .likeIfPresent(AiLearningResourceDO::getTitle, reqVO.getTitle())
                .orderByDesc(AiLearningResourceDO::getId));
    }
}
