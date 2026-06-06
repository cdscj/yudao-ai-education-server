package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiSubjectCategoryPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiSubjectCategoryRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiSubjectCategorySaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiSubjectCategoryDO;
import cn.iocoder.yudao.module.ai.service.education.AiSubjectCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 学科分类")
@RestController
@RequestMapping("/ai/education/subject-category")
@Validated
public class AdminAiSubjectCategoryController {

    @Resource
    private AiSubjectCategoryService subjectCategoryService;

    @PostMapping("/create")
    @Operation(summary = "创建学科分类")
    @PreAuthorize("@ss.hasPermission('ai:subject-category:create')")
    public CommonResult<Long> create(@Valid @RequestBody AiSubjectCategorySaveReqVO reqVO) {
        return success(subjectCategoryService.createSubjectCategory(BeanUtils.toBean(reqVO, AiSubjectCategoryDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学科分类")
    @PreAuthorize("@ss.hasPermission('ai:subject-category:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody AiSubjectCategorySaveReqVO reqVO) {
        subjectCategoryService.updateSubjectCategory(BeanUtils.toBean(reqVO, AiSubjectCategoryDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学科分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:subject-category:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        subjectCategoryService.deleteSubjectCategory(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学科分类")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:subject-category:query')")
    public CommonResult<AiSubjectCategoryRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(subjectCategoryService.getSubjectCategory(id), AiSubjectCategoryRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学科分类分页")
    @PreAuthorize("@ss.hasPermission('ai:subject-category:query')")
    public CommonResult<PageResult<AiSubjectCategoryRespVO>> page(@Valid AiSubjectCategoryPageReqVO pageReqVO) {
        return success(BeanUtils.toBean(subjectCategoryService.getSubjectCategoryPage(
                pageReqVO.getName(), pageReqVO.getStatus(), pageReqVO.getPageNo(), pageReqVO.getPageSize()),
                AiSubjectCategoryRespVO.class));
    }

    @GetMapping("/tree")
    @Operation(summary = "获得学科分类树")
    @PreAuthorize("@ss.hasPermission('ai:subject-category:query')")
    public CommonResult<List<AiSubjectCategoryRespVO>> tree() {
        return success(BeanUtils.toBean(subjectCategoryService.getSubjectCategoryTree(), AiSubjectCategoryRespVO.class));
    }
}
