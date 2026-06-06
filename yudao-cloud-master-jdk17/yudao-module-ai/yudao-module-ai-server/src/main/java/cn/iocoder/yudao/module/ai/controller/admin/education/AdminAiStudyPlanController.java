package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiStudyPlanRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiStudyPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 学习计划")
@RestController
@RequestMapping("/ai/education/study-plan")
@Validated
public class AdminAiStudyPlanController {

    @Resource private AiStudyPlanService service;

    @GetMapping("/page") @Operation(summary = "计划分页")
    @PreAuthorize("@ss.hasPermission('ai:study-plan:query')")
    public CommonResult<PageResult<AiStudyPlanRespVO>> page(
            @RequestParam(value="userId",required=false) Long userId,
            @RequestParam(value="planType",required=false) String planType,
            @RequestParam(value="status",required=false) String status,
            @RequestParam(value="pageNo",defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize",defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getPlanPage(userId, planType, status, pageNo, pageSize), AiStudyPlanRespVO.class));
    }

    @GetMapping("/get") @Operation(summary = "计划详情")
    @PreAuthorize("@ss.hasPermission('ai:study-plan:query')")
    public CommonResult<AiStudyPlanRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(service.getPlan(id), AiStudyPlanRespVO.class));
    }
}
