package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourceGenerateReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourcePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourceRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiLearningResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 学习资源")
@RestController
@RequestMapping("/ai/education/resource")
@Validated
public class AiLearningResourceController {

    @Resource
    private AiLearningResourceService learningResourceService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "生成学习资源（流式）")
    @PreAuthorize("@ss.hasPermission('ai:learning-resource:query')")
    public Flux<CommonResult<String>> generateResource(@RequestBody @Valid LearningResourceGenerateReqVO reqVO) {
        return learningResourceService.generateResource(reqVO, getLoginUserId());
    }

    @GetMapping("/get")
    @Operation(summary = "获取学习资源")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:learning-resource:query')")
    public CommonResult<LearningResourceRespVO> getResource(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(learningResourceService.getResource(id), LearningResourceRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学习资源分页")
    @PreAuthorize("@ss.hasPermission('ai:learning-resource:query')")
    public CommonResult<PageResult<LearningResourceRespVO>> getResourcePage(@Valid LearningResourcePageReqVO reqVO) {
        return success(BeanUtils.toBean(learningResourceService.getResourcePage(reqVO), LearningResourceRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学习资源")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:learning-resource:delete')")
    public CommonResult<Boolean> deleteResource(@RequestParam("id") Long id) {
        learningResourceService.deleteResource(id);
        return success(true);
    }
}
