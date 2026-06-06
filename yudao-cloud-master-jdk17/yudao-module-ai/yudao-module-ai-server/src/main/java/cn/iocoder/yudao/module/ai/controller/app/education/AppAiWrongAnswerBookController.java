package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiWrongAnswerBookRespVO;
import cn.iocoder.yudao.module.ai.controller.app.education.vo.AppWrongAnswerRecordReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;
import cn.iocoder.yudao.module.ai.service.education.AiWrongAnswerBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 错题本")
@RestController
@RequestMapping("/ai/education/wrong-answer")
@Validated
public class AppAiWrongAnswerBookController {

    @Resource
    private AiWrongAnswerBookService wrongAnswerBookService;

    @GetMapping("/page")
    @Operation(summary = "获得我的错题本分页")
    public CommonResult<PageResult<AiWrongAnswerBookRespVO>> page(
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "masteryLevel", required = false) Integer masteryLevel,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return success(BeanUtils.toBean(wrongAnswerBookService.getWrongAnswerPage(
                getLoginUserId(), subjectId, masteryLevel, pageNo, pageSize), AiWrongAnswerBookRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得错题详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    public CommonResult<AiWrongAnswerBookRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(wrongAnswerBookService.getWrongAnswer(id), AiWrongAnswerBookRespVO.class));
    }

    @PostMapping("/review")
    @Operation(summary = "复习错题")
    @Parameter(name = "id", description = "错题编号", required = true, example = "1")
    public CommonResult<Boolean> review(@RequestParam("id") Long id) {
        wrongAnswerBookService.reviewQuestion(id);
        return success(true);
    }

    @GetMapping("/stats")
    @Operation(summary = "获得我的错题统计")
    public CommonResult<Map<String, Object>> stats() {
        return success(wrongAnswerBookService.getStats(getLoginUserId()));
    }

    @PostMapping("/record")
    @Operation(summary = "记录答题（自动归集错题）")
    public CommonResult<Boolean> record(@RequestBody @Valid AppWrongAnswerRecordReqVO reqVO) {
        AiWrongAnswerBookDO record = new AiWrongAnswerBookDO();
        record.setUserId(getLoginUserId()).setQuestionId(reqVO.getQuestionId())
              .setSubjectId(reqVO.getSubjectId()).setUserAnswer(reqVO.getUserAnswer())
              .setCorrectAnswer(reqVO.getCorrectAnswer()).setIsCorrect(reqVO.getIsCorrect())
              .setKnowledgeTagIds(reqVO.getKnowledgeTagIds())
              .setSourceType(reqVO.getSourceType()).setSourceId(reqVO.getSourceId());
        wrongAnswerBookService.recordAnswer(record);
        return success(true);
    }
}
