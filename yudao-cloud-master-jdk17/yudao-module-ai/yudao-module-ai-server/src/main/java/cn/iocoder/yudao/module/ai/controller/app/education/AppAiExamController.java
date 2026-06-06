package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiExamRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiExamRecordRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamRecordDO;
import cn.iocoder.yudao.module.ai.controller.app.education.vo.AppExamSubmitReqVO;
import cn.iocoder.yudao.module.ai.service.education.AiExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 模拟考试")
@RestController
@RequestMapping("/ai/education/exam")
@Validated
public class AppAiExamController {

    @Resource private AiExamService service;

    @GetMapping("/list") @Operation(summary = "考试列表")
    public CommonResult<PageResult<AiExamRespVO>> list(
            @RequestParam(value="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getPublishedList(pageNo, pageSize), AiExamRespVO.class));
    }

    @GetMapping("/get") @Operation(summary = "考试详情")
    public CommonResult<AiExamRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(service.getExam(id), AiExamRespVO.class));
    }

    @PostMapping("/start") @Operation(summary = "开始考试")
    public CommonResult<Long> start(@RequestParam("examId") Long examId) {
        return success(service.startExam(examId, getLoginUserId()));
    }

    @PostMapping("/submit") @Operation(summary = "交卷")
    public CommonResult<Boolean> submit(@RequestBody @Valid AppExamSubmitReqVO reqVO) {
        service.submitExam(reqVO.getExamId(), getLoginUserId(), reqVO.getAnswers(),
                reqVO.getDurationSeconds() != null ? reqVO.getDurationSeconds() : 0);
        return success(true);
    }

    @GetMapping("/record-get") @Operation(summary = "考试记录")
    public CommonResult<AiExamRecordRespVO> recordGet(@RequestParam("examId") Long examId) {
        AiExamRecordDO r = service.getRecord(examId, getLoginUserId());
        return success(r != null ? BeanUtils.toBean(r, AiExamRecordRespVO.class) : null);
    }

    @GetMapping("/history") @Operation(summary = "考试历史")
    public CommonResult<PageResult<AiExamRecordRespVO>> history(
            @RequestParam(value="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getRecordPage(getLoginUserId(), pageNo, pageSize), AiExamRecordRespVO.class));
    }
}
