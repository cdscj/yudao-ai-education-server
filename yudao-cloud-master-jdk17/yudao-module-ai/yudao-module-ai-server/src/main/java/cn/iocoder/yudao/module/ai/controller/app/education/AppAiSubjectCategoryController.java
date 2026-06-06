package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiSubjectCategoryRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiSubjectCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 学科分类")
@RestController
@RequestMapping("/ai/education/subject-category")
@Validated
public class AppAiSubjectCategoryController {

    @Resource
    private AiSubjectCategoryService subjectCategoryService;

    @GetMapping("/tree")
    @Operation(summary = "获得学科分类树")
    public CommonResult<List<AiSubjectCategoryRespVO>> tree() {
        return success(BeanUtils.toBean(subjectCategoryService.getSubjectCategoryTree(), AiSubjectCategoryRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得启用的学科分类列表")
    public CommonResult<List<AiSubjectCategoryRespVO>> list() {
        return success(BeanUtils.toBean(subjectCategoryService.getEnabledList(), AiSubjectCategoryRespVO.class));
    }
}
