package cn.iocoder.yudao.module.ai.controller.admin.config;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplateRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiPromptTemplateDO;
import cn.iocoder.yudao.module.ai.service.config.AiPromptTemplateService;
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

@Tag(name = "管理后台 - AI 提示词模板")
@RestController
@RequestMapping("/ai/prompt-template")
@Validated
public class AiPromptTemplateController {

    @Resource
    private AiPromptTemplateService promptTemplateService;

    @PostMapping("/create")
    @Operation(summary = "创建提示词模板")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:create')")
    public CommonResult<Long> createPromptTemplate(@Valid @RequestBody AiPromptTemplateSaveReqVO createReqVO) {
        return success(promptTemplateService.createPromptTemplate(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新提示词模板")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:update')")
    public CommonResult<Boolean> updatePromptTemplate(@Valid @RequestBody AiPromptTemplateSaveReqVO updateReqVO) {
        promptTemplateService.updatePromptTemplate(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除提示词模板")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:delete')")
    public CommonResult<Boolean> deletePromptTemplate(@RequestParam("id") Long id) {
        promptTemplateService.deletePromptTemplate(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得提示词模板")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:query')")
    public CommonResult<AiPromptTemplateRespVO> getPromptTemplate(@RequestParam("id") Long id) {
        AiPromptTemplateDO template = promptTemplateService.getPromptTemplate(id);
        return success(BeanUtils.toBean(template, AiPromptTemplateRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得提示词模板分页")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:query')")
    public CommonResult<PageResult<AiPromptTemplateRespVO>> getPromptTemplatePage(@Valid AiPromptTemplatePageReqVO pageReqVO) {
        PageResult<AiPromptTemplateDO> pageResult = promptTemplateService.getPromptTemplatePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiPromptTemplateRespVO.class));
    }

    @GetMapping("/list-by-category")
    @Operation(summary = "根据分类获得提示词模板列表")
    @Parameter(name = "category", description = "分类", required = true, example = "writing")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:query')")
    public CommonResult<List<AiPromptTemplateRespVO>> getPromptTemplateListByCategory(@RequestParam("category") String category) {
        List<AiPromptTemplateDO> list = promptTemplateService.getPromptTemplateListByCategory(category);
        return success(BeanUtils.toBean(list, AiPromptTemplateRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得启用的提示词模板列表")
    @Parameter(name = "type", description = "类型", example = "1")
    @PreAuthorize("@ss.hasPermission('ai:prompt-template:query')")
    public CommonResult<List<AiPromptTemplateRespVO>> getEnabledPromptTemplateList(
            @RequestParam(value = "type", required = false) Integer type) {
        List<AiPromptTemplateDO> list = promptTemplateService.getEnabledPromptTemplateList(type);
        return success(BeanUtils.toBean(list, AiPromptTemplateRespVO.class));
    }

}
