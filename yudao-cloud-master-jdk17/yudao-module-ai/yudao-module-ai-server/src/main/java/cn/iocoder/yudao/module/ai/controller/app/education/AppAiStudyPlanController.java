package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiStudyPlanRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudyPlanDO;
import cn.iocoder.yudao.module.ai.controller.app.education.vo.AppStudyPlanCreateReqVO;
import cn.iocoder.yudao.module.ai.service.education.AiStudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 学习计划")
@RestController
@RequestMapping("/ai/education/study-plan")
@Validated
public class AppAiStudyPlanController {

    @Resource private AiStudyPlanService service;

    @GetMapping("/active") @Operation(summary = "当前计划")
    public CommonResult<AiStudyPlanRespVO> active() {
        AiStudyPlanDO p = service.getActivePlan(getLoginUserId());
        return success(p != null ? BeanUtils.toBean(p, AiStudyPlanRespVO.class) : null);
    }

    @GetMapping("/get") @Operation(summary = "计划详情")
    public CommonResult<AiStudyPlanRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(service.getPlan(id), AiStudyPlanRespVO.class));
    }

    @PostMapping("/create") @Operation(summary = "创建计划")
    public CommonResult<Long> create(@RequestBody @Valid AppStudyPlanCreateReqVO reqVO) {
        AiStudyPlanDO p = new AiStudyPlanDO();
        p.setUserId(getLoginUserId()).setTitle(reqVO.getTitle())
         .setPlanType(reqVO.getPlanType() != null ? reqVO.getPlanType() : "WEEKLY")
         .setStartDate(java.time.LocalDate.parse(reqVO.getStartDate()))
         .setEndDate(java.time.LocalDate.parse(reqVO.getEndDate()))
         .setGoal(reqVO.getGoal() != null ? reqVO.getGoal() : "")
         .setDescription(reqVO.getDescription() != null ? reqVO.getDescription() : "")
         .setStatus("ACTIVE").setProgress(0).setSource("MANUAL");
        return success(service.createPlan(p));
    }

    @PutMapping("/complete") @Operation(summary = "完成计划")
    public CommonResult<Boolean> complete(@RequestParam("id") Long id) {
        service.completePlan(id, getLoginUserId()); return success(true);
    }

    @GetMapping("/history") @Operation(summary = "计划历史")
    public CommonResult<PageResult<AiStudyPlanRespVO>> history(
            @RequestParam(value="pageNo",defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize",defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getPlanPage(getLoginUserId(), null, null, pageNo, pageSize), AiStudyPlanRespVO.class));
    }
}
