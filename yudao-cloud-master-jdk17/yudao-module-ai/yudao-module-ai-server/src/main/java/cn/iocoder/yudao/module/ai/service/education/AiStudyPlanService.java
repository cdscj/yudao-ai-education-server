package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudyPlanDO;

import java.util.List;

public interface AiStudyPlanService {
    Long createPlan(AiStudyPlanDO plan);
    void updatePlan(AiStudyPlanDO plan);
    void completePlan(Long id, Long userId);
    AiStudyPlanDO getActivePlan(Long userId);
    AiStudyPlanDO getPlan(Long id);
    PageResult<AiStudyPlanDO> getPlanPage(Long userId, String planType, String status, Integer pageNo, Integer pageSize);

    /**
     * 根据每日报告 + 历史计划，AI 生成/更新学习计划
     * @return 生成的计划
     */
    AiStudyPlanDO generateFromReport(Long userId);

    /**
     * 批量生成：找出近7天活跃的学生，跳过不活跃的
     * @return 生成数量
     */
    int batchGeneratePlans();

    /**
     * 获取近7天有学习活动的活跃用户ID列表
     */
    List<Long> getActiveUserIds();
}
