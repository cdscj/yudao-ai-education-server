package cn.iocoder.yudao.module.ai.controller.admin.school;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiSchoolPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiSchoolRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiSchoolSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiSchoolDO;
import cn.iocoder.yudao.module.ai.service.school.AiSchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 学校管理")
@RestController
@RequestMapping("/ai/school")
@Validated
public class AdminSchoolController {

    @Resource
    private AiSchoolService schoolService;

    @PostMapping("/create")
    @Operation(summary = "创建学校")
    @PreAuthorize("@ss.hasPermission('ai:school:create')")
    public CommonResult<Long> createSchool(@Valid @RequestBody AiSchoolSaveReqVO reqVO) {
        AiSchoolDO school = BeanUtils.toBean(reqVO, AiSchoolDO.class);
        return success(schoolService.createSchool(school));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学校")
    @PreAuthorize("@ss.hasPermission('ai:school:update')")
    public CommonResult<Boolean> updateSchool(@Valid @RequestBody AiSchoolSaveReqVO reqVO) {
        AiSchoolDO school = BeanUtils.toBean(reqVO, AiSchoolDO.class);
        schoolService.updateSchool(school);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学校")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:school:delete')")
    public CommonResult<Boolean> deleteSchool(@RequestParam("id") Long id) {
        schoolService.deleteSchool(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学校")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:school:query')")
    public CommonResult<AiSchoolRespVO> getSchool(@RequestParam("id") Long id) {
        AiSchoolDO school = schoolService.getSchool(id);
        return success(BeanUtils.toBean(school, AiSchoolRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学校分页")
    @PreAuthorize("@ss.hasPermission('ai:school:query')")
    public CommonResult<PageResult<AiSchoolRespVO>> getSchoolPage(@Valid AiSchoolPageReqVO pageReqVO) {
        PageResult<AiSchoolDO> pageResult = schoolService.getSchoolList(
                pageReqVO.getName(), pageReqVO.getType(), pageReqVO.getCity(),
                pageReqVO.getPageNo(), pageReqVO.getPageSize());
        return success(BeanUtils.toBean(pageResult, AiSchoolRespVO.class));
    }

}
