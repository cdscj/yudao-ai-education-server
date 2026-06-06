package cn.iocoder.yudao.module.ai.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.goal.AppAiGoalProgressReqVO;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.goal.AppAiGoalRespVO;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.goal.AppAiGoalSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLearningGoalDO;
import cn.iocoder.yudao.module.ai.service.social.AiLearningGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - AI 学习目标")
@RestController
@RequestMapping("/ai/social/goal")
@Validated
public class AppAiGoalController {

    @Resource
    private AiLearningGoalService learningGoalService;

    @PostMapping("/create")
    @Operation(summary = "创建学习目标")
    public CommonResult<Long> createGoal(@Valid @RequestBody AppAiGoalSaveReqVO reqVO) {
        AiLearningGoalDO goal = BeanUtils.toBean(reqVO, AiLearningGoalDO.class);
        return success(learningGoalService.createGoal(getLoginUserId(), goal));
    }

    @PutMapping("/progress")
    @Operation(summary = "更新学习进度")
    @Parameter(name = "goalId", description = "目标编号", required = true, example = "1")
    public CommonResult<Boolean> updateProgress(
            @RequestParam("goalId") Long goalId,
            @Valid @RequestBody AppAiGoalProgressReqVO reqVO) {
        learningGoalService.updateProgress(goalId, reqVO.getCurrentValue());
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取学习目标列表")
    public CommonResult<List<AppAiGoalRespVO>> getGoalList() {
        List<AiLearningGoalDO> list = learningGoalService.getGoalList(getLoginUserId());
        return success(BeanUtils.toBean(list, AppAiGoalRespVO.class));
    }

    @PostMapping("/complete")
    @Operation(summary = "完成学习目标")
    @Parameter(name = "goalId", description = "目标编号", required = true, example = "1")
    public CommonResult<Boolean> completeGoal(@RequestParam("goalId") Long goalId) {
        learningGoalService.completeGoal(goalId);
        return success(true);
    }

    @GetMapping("/stats")
    @Operation(summary = "获取学习目标统计")
    public CommonResult<Map<String, Object>> getGoalStats() {
        return success(learningGoalService.getGoalStats(getLoginUserId()));
    }
}
