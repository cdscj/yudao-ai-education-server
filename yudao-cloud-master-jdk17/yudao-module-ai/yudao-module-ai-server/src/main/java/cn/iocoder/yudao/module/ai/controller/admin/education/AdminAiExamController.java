package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamDO;
import cn.iocoder.yudao.module.ai.service.education.AiExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 模拟考试")
@RestController
@RequestMapping("/ai/education/exam")
@Validated
public class AdminAiExamController {

    @Resource private AiExamService service;

    @PostMapping("/create") @Operation(summary = "创建考试")
    @PreAuthorize("@ss.hasPermission('ai:exam:create')")
    public CommonResult<Long> create(@Valid @RequestBody AiExamSaveReqVO vo) {
        return success(service.createExam(BeanUtils.toBean(vo, AiExamDO.class)));
    }

    @PutMapping("/update") @Operation(summary = "更新考试")
    @PreAuthorize("@ss.hasPermission('ai:exam:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody AiExamSaveReqVO vo) {
        service.updateExam(BeanUtils.toBean(vo, AiExamDO.class)); return success(true);
    }

    @DeleteMapping("/delete") @Operation(summary = "删除考试")
    @PreAuthorize("@ss.hasPermission('ai:exam:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) { service.deleteExam(id); return success(true); }

    @PostMapping("/publish") @Operation(summary = "发布考试")
    @PreAuthorize("@ss.hasPermission('ai:exam:update')")
    public CommonResult<Boolean> publish(@RequestParam("id") Long id) { service.publishExam(id); return success(true); }

    @GetMapping("/get") @Operation(summary = "获得考试")
    @PreAuthorize("@ss.hasPermission('ai:exam:query')")
    public CommonResult<AiExamRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(service.getExam(id), AiExamRespVO.class));
    }

    @GetMapping("/page") @Operation(summary = "考试分页")
    @PreAuthorize("@ss.hasPermission('ai:exam:query')")
    public CommonResult<PageResult<AiExamRespVO>> page(
            @RequestParam(value="subjectId", required=false) Long subjectId,
            @RequestParam(value="publishStatus", required=false) String publishStatus,
            @RequestParam(value="title", required=false) String title,
            @RequestParam(value="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getExamPage(subjectId, publishStatus, title, pageNo, pageSize), AiExamRespVO.class));
    }

    @PostMapping("/ai-generate") @Operation(summary = "AI 生成考试题目")
    @PreAuthorize("@ss.hasPermission('ai:exam:update')")
    public CommonResult<Boolean> aiGenerate(@RequestParam("examId") Long examId,
            @RequestParam("subjectId") Long subjectId,
            @RequestParam(value="difficulty", defaultValue="3") Integer difficulty,
            @RequestParam(value="questionCount", defaultValue="10") Integer questionCount) {
        service.generateExamByAI(examId, subjectId, difficulty, questionCount);
        return success(true);
    }

    @PostMapping("/analyze-record") @Operation(summary = "AI 分析考试记录")
    @PreAuthorize("@ss.hasPermission('ai:exam:update')")
    public CommonResult<Boolean> analyzeRecord(@RequestParam("id") Long recordId) {
        service.analyzeExamRecord(recordId);
        return success(true);
    }
}
