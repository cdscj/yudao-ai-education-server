package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningPathDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiLearningPathMapper extends BaseMapperX<AiLearningPathDO> {

    default List<AiLearningPathDO> selectListByUserId(Long userId) {
        return selectList(AiLearningPathDO::getUserId, userId);
    }

    default PageResult<AiLearningPathDO> selectPage(LearningPathPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiLearningPathDO>()
                .eqIfPresent(AiLearningPathDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiLearningPathDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiLearningPathDO::getId));
    }
}
