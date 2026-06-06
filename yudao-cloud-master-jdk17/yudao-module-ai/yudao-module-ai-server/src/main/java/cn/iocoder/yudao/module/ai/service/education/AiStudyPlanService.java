package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudyPlanDO;

public interface AiStudyPlanService {
    Long createPlan(AiStudyPlanDO plan);
    void updatePlan(AiStudyPlanDO plan);
    void completePlan(Long id, Long userId);
    AiStudyPlanDO getActivePlan(Long userId);
    AiStudyPlanDO getPlan(Long id);
    PageResult<AiStudyPlanDO> getPlanPage(Long userId, String planType, String status, Integer pageNo, Integer pageSize);
}
