package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudyPlanDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiStudyPlanMapper extends BaseMapperX<AiStudyPlanDO> {

    default PageResult<AiStudyPlanDO> selectPage(PageParam pp, Long userId, String planType, String status) {
        return selectPage(pp, new LambdaQueryWrapperX<AiStudyPlanDO>()
                .eqIfPresent(AiStudyPlanDO::getUserId, userId)
                .eqIfPresent(AiStudyPlanDO::getPlanType, planType)
                .eqIfPresent(AiStudyPlanDO::getStatus, status)
                .orderByDesc(AiStudyPlanDO::getId));
    }

    default AiStudyPlanDO selectActiveByUserId(Long userId) {
        return selectOne(new LambdaQueryWrapperX<AiStudyPlanDO>()
                .eq(AiStudyPlanDO::getUserId, userId)
                .eq(AiStudyPlanDO::getStatus, "ACTIVE"));
    }

    default List<AiStudyPlanDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiStudyPlanDO>()
                .eq(AiStudyPlanDO::getUserId, userId)
                .orderByDesc(AiStudyPlanDO::getId));
    }
}
