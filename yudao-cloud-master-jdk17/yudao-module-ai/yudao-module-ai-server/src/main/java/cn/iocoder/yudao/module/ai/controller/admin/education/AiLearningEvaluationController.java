package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.EvaluationPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.EvaluationRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiLearningEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
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

@Tag(name = "管理后台 - 学习评估")
@RestController
@RequestMapping("/ai/education/evaluation")
@Validated
public class AiLearningEvaluationController {

    @Resource
    private AiLearningEvaluationService evaluationService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "生成学习评估（流式）")
    @PreAuthorize("@ss.hasPermission('ai:learning-evaluation:query')")
    public Flux<CommonResult<String>> generateEvaluation() {
        return evaluationService.generateEvaluation(getLoginUserId());
    }

    @GetMapping("/page")
    @Operation(summary = "获得学习评估分页")
    @PreAuthorize("@ss.hasPermission('ai:learning-evaluation:query')")
    public CommonResult<PageResult<EvaluationRespVO>> getEvaluationPage(@Valid EvaluationPageReqVO reqVO) {
        return success(BeanUtils.toBean(evaluationService.getEvaluationPage(reqVO), EvaluationRespVO.class));
    }
}
