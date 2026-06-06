package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.goal.AiGoalPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLearningGoalDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiLearningGoalMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.LEARNING_GOAL_ALREADY_COMPLETED;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.LEARNING_GOAL_NOT_EXISTS;

/**
 * AI 学习目标 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AiLearningGoalServiceImpl implements AiLearningGoalService {

    @Resource
    private AiLearningGoalMapper learningGoalMapper;
    @Resource
    private AiUserPointsService userPointsService;
    @Resource
    private AiUserActivityService userActivityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createGoal(Long userId, AiLearningGoalDO goal) {
        goal.setUserId(userId);
        goal.setCurrentValue(0);
        goal.setStatus("ACTIVE");
        learningGoalMapper.insert(goal);
        return goal.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProgress(Long goalId, Integer currentValue) {
        AiLearningGoalDO goal = learningGoalMapper.selectById(goalId);
        if (goal == null) {
            throw exception(LEARNING_GOAL_NOT_EXISTS);
        }
        goal.setCurrentValue(currentValue);
        learningGoalMapper.updateById(goal);
    }

    @Override
    public List<AiLearningGoalDO> getGoalList(Long userId) {
        return learningGoalMapper.selectListByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void completeGoal(Long goalId) {
        AiLearningGoalDO goal = learningGoalMapper.selectById(goalId);
        if (goal == null) {
            throw exception(LEARNING_GOAL_NOT_EXISTS);
        }
        if ("COMPLETED".equals(goal.getStatus())) {
            throw exception(LEARNING_GOAL_ALREADY_COMPLETED);
        }

        goal.setStatus("COMPLETED");
        learningGoalMapper.updateById(goal);

        // 完成目标奖励积分
        userPointsService.addPoints(goal.getUserId(), 50, 2, goalId);

        // 发布动态
        String content = "完成了学习目标：「" + goal.getTitle() + "」！";
        userActivityService.publishActivity(goal.getUserId(), 2, content, goalId);
    }

    @Override
    public Map<String, Object> getGoalStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        long total = learningGoalMapper.selectListByUserId(userId).size();
        long completed = learningGoalMapper.selectCountByUserIdAndStatus(userId, "COMPLETED");
        long active = learningGoalMapper.selectCountByUserIdAndStatus(userId, "ACTIVE");
        long failed = learningGoalMapper.selectCountByUserIdAndStatus(userId, "FAILED");

        stats.put("total", total);
        stats.put("completed", completed);
        stats.put("active", active);
        stats.put("failed", failed);
        return stats;
    }

    @Override
    public PageResult<AiLearningGoalDO> getGoalPage(AiGoalPageReqVO pageReqVO) {
        return learningGoalMapper.selectPage(pageReqVO, new LambdaQueryWrapperX<AiLearningGoalDO>()
                .eqIfPresent(AiLearningGoalDO::getUserId, pageReqVO.getUserId())
                .eqIfPresent(AiLearningGoalDO::getGoalType, pageReqVO.getGoalType())
                .eqIfPresent(AiLearningGoalDO::getStatus, pageReqVO.getStatus())
                .orderByDesc(AiLearningGoalDO::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteGoal(Long id) {
        AiLearningGoalDO goal = learningGoalMapper.selectById(id);
        if (goal == null) {
            throw exception(LEARNING_GOAL_NOT_EXISTS);
        }
        learningGoalMapper.deleteById(id);
    }
}
