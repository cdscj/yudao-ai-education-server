package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiHomeworkRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiHomeworkSubmissionRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkSubmissionDO;
import cn.iocoder.yudao.module.ai.controller.app.education.vo.AppHomeworkSubmitReqVO;
import cn.iocoder.yudao.module.ai.service.education.AiHomeworkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 作业")
@RestController
@RequestMapping("/ai/education/homework")
@Validated
public class AppAiHomeworkController {

    @Resource private AiHomeworkService service;

    @GetMapping("/my-page") @Operation(summary = "我的作业列表")
    public CommonResult<PageResult<AiHomeworkRespVO>> myPage(
            @RequestParam(value="subjectId", required=false) Long subjectId,
            @RequestParam(value="pageNo", defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize", defaultValue="10") Integer pageSize) {
        return success(BeanUtils.toBean(service.getPublishedPage(subjectId, pageNo, pageSize), AiHomeworkRespVO.class));
    }

    @GetMapping("/my-get") @Operation(summary = "作业详情")
    public CommonResult<AiHomeworkRespVO> myGet(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(service.getHomework(id), AiHomeworkRespVO.class));
    }

    @GetMapping("/my-submission") @Operation(summary = "我的提交")
    public CommonResult<AiHomeworkSubmissionRespVO> mySubmission(@RequestParam("homeworkId") Long homeworkId) {
        AiHomeworkSubmissionDO sub = service.getSubmission(homeworkId, getLoginUserId());
        return success(sub != null ? BeanUtils.toBean(sub, AiHomeworkSubmissionRespVO.class) : null);
    }

    @PostMapping("/submit") @Operation(summary = "提交作业")
    public CommonResult<Long> submit(@RequestBody @Valid AppHomeworkSubmitReqVO reqVO) {
        return success(service.submitHomework(reqVO.getHomeworkId(), getLoginUserId(),
                reqVO.getAnswers(), reqVO.getDurationSeconds() != null ? reqVO.getDurationSeconds() : 0));
    }
}
