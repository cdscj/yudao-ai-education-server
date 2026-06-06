package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiQuestionBankRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.service.education.AiQuestionBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 题库")
@RestController
@RequestMapping("/ai/education/question-bank")
@Validated
public class AppAiQuestionBankController {

    @Resource
    private AiQuestionBankService questionBankService;

    @GetMapping("/page")
    @Operation(summary = "获得题库分页（学生只读）")
    public CommonResult<PageResult<AiQuestionBankRespVO>> page(
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "questionType", required = false) String questionType,
            @RequestParam(value = "difficulty", required = false) Integer difficulty,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return success(BeanUtils.toBean(questionBankService.getQuestionPage(
                subjectId, questionType, difficulty, keyword, null, pageNo, pageSize),
                AiQuestionBankRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得题目详情")
    @Parameter(name = "id", description = "题目编号", required = true, example = "1")
    public CommonResult<AiQuestionBankRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(questionBankService.getQuestion(id), AiQuestionBankRespVO.class));
    }
}
