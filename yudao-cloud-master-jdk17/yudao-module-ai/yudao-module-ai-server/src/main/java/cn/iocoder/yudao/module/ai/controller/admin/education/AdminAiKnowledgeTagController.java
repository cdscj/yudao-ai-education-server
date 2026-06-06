package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiKnowledgeTagPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiKnowledgeTagRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiKnowledgeTagSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;
import cn.iocoder.yudao.module.ai.service.education.AiKnowledgeTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 知识点标签")
@RestController
@RequestMapping("/ai/education/knowledge-tag")
@Validated
public class AdminAiKnowledgeTagController {

    @Resource
    private AiKnowledgeTagService knowledgeTagService;

    @PostMapping("/create")
    @Operation(summary = "创建知识点标签")
    @PreAuthorize("@ss.hasPermission('ai:knowledge-tag:create')")
    public CommonResult<Long> create(@Valid @RequestBody AiKnowledgeTagSaveReqVO reqVO) {
        return success(knowledgeTagService.createKnowledgeTag(BeanUtils.toBean(reqVO, AiKnowledgeTagDO.class)));
    }

    @PutMapping("/update")
    @Operation(summary = "更新知识点标签")
    @PreAuthorize("@ss.hasPermission('ai:knowledge-tag:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody AiKnowledgeTagSaveReqVO reqVO) {
        knowledgeTagService.updateKnowledgeTag(BeanUtils.toBean(reqVO, AiKnowledgeTagDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除知识点标签")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:knowledge-tag:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        knowledgeTagService.deleteKnowledgeTag(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得知识点标签")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:knowledge-tag:query')")
    public CommonResult<AiKnowledgeTagRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(knowledgeTagService.getKnowledgeTag(id), AiKnowledgeTagRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得知识点标签分页")
    @PreAuthorize("@ss.hasPermission('ai:knowledge-tag:query')")
    public CommonResult<PageResult<AiKnowledgeTagRespVO>> page(@Valid AiKnowledgeTagPageReqVO pageReqVO) {
        return success(BeanUtils.toBean(knowledgeTagService.getKnowledgeTagPage(
                pageReqVO.getSubjectId(), pageReqVO.getName(), pageReqVO.getStatus(),
                pageReqVO.getPageNo(), pageReqVO.getPageSize()), AiKnowledgeTagRespVO.class));
    }
}
