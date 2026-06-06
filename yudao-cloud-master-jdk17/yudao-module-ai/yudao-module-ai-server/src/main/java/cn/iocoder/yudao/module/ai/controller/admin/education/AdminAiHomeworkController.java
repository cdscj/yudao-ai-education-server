package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkDO;
import cn.iocoder.yudao.module.ai.service.education.AiHomeworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 作业管理")
@RestController
@RequestMapping("/ai/education/homework")
@Validated
public class AdminAiHomeworkController {

    @Resource private AiHomeworkService service;

    @PostMapping("/create") @Operation(summary = "创建作业")
    @PreAuthorize("@ss.hasPermission('ai:homework:create')")
    public CommonResult<Long> create(@Valid @RequestBody AiHomeworkSaveReqVO vo) {
        return success(service.createHomework(BeanUtils.toBean(vo, AiHomeworkDO.class)));
    }

    @PutMapping("/update") @Operation(summary = "更新作业")
    @PreAuthorize("@ss.hasPermission('ai:homework:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody AiHomeworkSaveReqVO vo) {
        service.updateHomework(BeanUtils.toBean(vo, AiHomeworkDO.class)); return success(true);
    }

    @DeleteMapping("/delete") @Operation(summary = "删除作业")
    @PreAuthorize("@ss.hasPermission('ai:homework:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) { service.deleteHomework(id); return success(true); }

    @PostMapping("/publish") @Operation(summary = "发布作业")
    @PreAuthorize("@ss.hasPermission('ai:homework:update')")
    public CommonResult<Boolean> publish(@RequestParam("id") Long id) { service.publishHomework(id); return success(true); }

    @GetMapping("/get") @Operation(summary = "获得作业")
    @PreAuthorize("@ss.hasPermission('ai:homework:query')")
    public CommonResult<AiHomeworkRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(service.getHomework(id), AiHomeworkRespVO.class));
    }

    @GetMapping("/page") @Operation(summary = "作业分页")
    @PreAuthorize("@ss.hasPermission('ai:homework:query')")
    public CommonResult<PageResult<AiHomeworkRespVO>> page(
            @RequestParam(value="subjectId", required=false) Long subjectId,
            @RequestParam(value="publishStatus", required=false) String publishStatus,
            @RequestParam(value="title", required=false) String title,
            @RequestParam(value="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getHomeworkPage(subjectId, publishStatus, title, pageNo, pageSize), AiHomeworkRespVO.class));
    }

    @GetMapping("/submission-page") @Operation(summary = "提交记录分页")
    @PreAuthorize("@ss.hasPermission('ai:homework:query')")
    public CommonResult<PageResult<AiHomeworkSubmissionRespVO>> submissionPage(
            @RequestParam(value="homeworkId", required=false) Long homeworkId,
            @RequestParam(value="userId", required=false) Long userId,
            @RequestParam(value="gradeStatus", required=false) String gradeStatus,
            @RequestParam(value="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getSubmissionPage(homeworkId, userId, gradeStatus, pageNo, pageSize), AiHomeworkSubmissionRespVO.class));
    }

    @PostMapping("/grade") @Operation(summary = "AI 批改提交")
    @PreAuthorize("@ss.hasPermission('ai:homework:update')")
    public CommonResult<Boolean> grade(@RequestParam("id") Long submissionId) {
        service.gradeSubmission(submissionId);
        return success(true);
    }
}
