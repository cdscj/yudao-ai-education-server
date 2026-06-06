package cn.iocoder.yudao.module.ai.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.goal.AiGoalPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.goal.AiGoalRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLearningGoalDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiLearningGoalMapper;
import cn.iocoder.yudao.module.ai.service.social.AiLearningGoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 学习目标")
@RestController
@RequestMapping("/ai/social/goal")
@Validated
public class AdminAiGoalController {

    @Resource
    private AiLearningGoalService learningGoalService;

    @Resource
    private AiLearningGoalMapper learningGoalMapper;

    @GetMapping("/page")
    @Operation(summary = "获得学习目标分页")
    @PreAuthorize("@ss.hasPermission('ai:goal:query')")
    public CommonResult<PageResult<AiGoalRespVO>> getGoalPage(@Valid AiGoalPageReqVO pageReqVO) {
        PageResult<AiLearningGoalDO> pageResult = learningGoalService.getGoalPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiGoalRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学习目标")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:goal:delete')")
    public CommonResult<Boolean> deleteGoal(@RequestParam("id") Long id) {
        learningGoalService.deleteGoal(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学习目标")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:goal:query')")
    public CommonResult<AiGoalRespVO> getGoal(@RequestParam("id") Long id) {
        AiLearningGoalDO goal = learningGoalMapper.selectById(id);
        return success(BeanUtils.toBean(goal, AiGoalRespVO.class));
    }

}
