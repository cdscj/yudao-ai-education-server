package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathGenerateReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningPathDO;
import cn.iocoder.yudao.module.ai.service.education.AiLearningPathService;
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

@Tag(name = "管理后台 - 学习路径")
@RestController
@RequestMapping("/ai/education/path")
@Validated
public class AiLearningPathController {

    @Resource
    private AiLearningPathService learningPathService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "生成学习路径（流式）")
    @PreAuthorize("@ss.hasPermission('ai:learning-path:query')")
    public Flux<CommonResult<String>> generatePath(@RequestBody @Valid LearningPathGenerateReqVO reqVO) {
        return learningPathService.generatePath(reqVO, getLoginUserId());
    }

    @GetMapping("/get")
    @Operation(summary = "获取学习路径")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:learning-path:query')")
    public CommonResult<LearningPathRespVO> getPath(@RequestParam("id") Long id) {
        AiLearningPathDO path = learningPathService.getPath(id);
        return success(BeanUtils.toBean(path, LearningPathRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学习路径分页")
    @PreAuthorize("@ss.hasPermission('ai:learning-path:query')")
    public CommonResult<PageResult<LearningPathRespVO>> getPathPage(@Valid LearningPathPageReqVO reqVO) {
        return success(BeanUtils.toBean(learningPathService.getPathPage(reqVO), LearningPathRespVO.class));
    }

    @PutMapping("/update-node-status")
    @Operation(summary = "更新节点进度")
    @PreAuthorize("@ss.hasPermission('ai:learning-path:update')")
    public CommonResult<Boolean> updateNodeStatus(@RequestParam("nodeId") Long nodeId, @RequestParam("status") String status) {
        learningPathService.updateNodeStatus(nodeId, status);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学习路径")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:learning-path:delete')")
    public CommonResult<Boolean> deletePath(@RequestParam("id") Long id) {
        learningPathService.deletePath(id);
        return success(true);
    }
}
