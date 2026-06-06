package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.goal.AiGoalPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLearningGoalDO;

import java.util.List;
import java.util.Map;

/**
 * AI 学习目标 Service 接口
 *
 * @author 芋道源码
 */
public interface AiLearningGoalService {

    /**
     * 创建学习目标
     *
     * @param userId 用户编号
     * @param goal   学习目标
     * @return 目标编号
     */
    Long createGoal(Long userId, AiLearningGoalDO goal);

    /**
     * 更新进度
     *
     * @param goalId       目标编号
     * @param currentValue 当前值
     */
    void updateProgress(Long goalId, Integer currentValue);

    /**
     * 获取用户的学习目标列表
     *
     * @param userId 用户编号
     * @return 学习目标列表
     */
    List<AiLearningGoalDO> getGoalList(Long userId);

    /**
     * 完成学习目标
     *
     * @param goalId 目标编号
     */
    void completeGoal(Long goalId);

    /**
     * 获取学习目标统计
     *
     * @param userId 用户编号
     * @return 统计信息
     */
    Map<String, Object> getGoalStats(Long userId);

    /**
     * 获得学习目标分页
     *
     * @param pageReqVO 分页请求
     * @return 学习目标分页
     */
    PageResult<AiLearningGoalDO> getGoalPage(AiGoalPageReqVO pageReqVO);

    /**
     * 删除学习目标
     *
     * @param id 目标编号
     */
    void deleteGoal(Long id);

}
