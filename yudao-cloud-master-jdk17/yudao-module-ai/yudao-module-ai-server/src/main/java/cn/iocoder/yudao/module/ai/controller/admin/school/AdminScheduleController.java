package cn.iocoder.yudao.module.ai.controller.admin.school;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiScheduleImportReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiSchedulePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiScheduleRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiCourseScheduleDO;
import cn.iocoder.yudao.module.ai.dal.mysql.school.AiCourseScheduleMapper;
import cn.iocoder.yudao.module.ai.service.school.AiCourseScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 课程表管理")
@RestController
@RequestMapping("/ai/schedule")
@Validated
public class AdminScheduleController {

    @Resource
    private AiCourseScheduleMapper courseScheduleMapper;

    @Resource
    private AiCourseScheduleService courseScheduleService;

    @GetMapping("/page")
    @Operation(summary = "获得课程表分页")
    @PreAuthorize("@ss.hasPermission('ai:schedule:query')")
    public CommonResult<PageResult<AiScheduleRespVO>> getSchedulePage(@Valid AiSchedulePageReqVO pageReqVO) {
        PageResult<AiCourseScheduleDO> pageResult = courseScheduleMapper.selectPage(pageReqVO,
                pageReqVO.getUserId(), pageReqVO.getCourseName(),
                pageReqVO.getDayOfWeek(), pageReqVO.getSemester());
        return success(BeanUtils.toBean(pageResult, AiScheduleRespVO.class));
    }

    @PostMapping("/import")
    @Operation(summary = "批量导入课程表")
    @PreAuthorize("@ss.hasPermission('ai:schedule:create')")
    public CommonResult<Integer> importSchedule(@RequestBody @Valid AiScheduleImportReqVO reqVO) {
        int count = courseScheduleService.importCourses(reqVO);
        return success(count);
    }

}
