package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiQuestionBankRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiQuestionBankSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.service.education.AiQuestionBankService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 题库管理")
@RestController
@RequestMapping("/ai/education/question-bank")
@Validated
public class AdminAiQuestionBankController {

    @Resource
    private AiQuestionBankService questionBankService;

    @PostMapping("/create")
    @Operation(summary = "创建题目")
    @PreAuthorize("@ss.hasPermission('ai:question-bank:create')")
    public CommonResult<Long> create(@Valid @RequestBody AiQuestionBankSaveReqVO reqVO) {
        AiQuestionBankDO question = BeanUtils.toBean(reqVO, AiQuestionBankDO.class);
        question.setSource("MANUAL");
        return success(questionBankService.createQuestion(question));
    }

    @PutMapping("/update")
    @Operation(summary = "更新题目")
    @PreAuthorize("@ss.hasPermission('ai:question-bank:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody AiQuestionBankSaveReqVO reqVO) {
        questionBankService.updateQuestion(BeanUtils.toBean(reqVO, AiQuestionBankDO.class));
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除题目")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:question-bank:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        questionBankService.deleteQuestion(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得题目")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:question-bank:query')")
    public CommonResult<AiQuestionBankRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(questionBankService.getQuestion(id), AiQuestionBankRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得题库分页")
    @PreAuthorize("@ss.hasPermission('ai:question-bank:query')")
    public CommonResult<PageResult<AiQuestionBankRespVO>> page(
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "questionType", required = false) String questionType,
            @RequestParam(value = "difficulty", required = false) Integer difficulty,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return success(BeanUtils.toBean(questionBankService.getQuestionPage(
                subjectId, questionType, difficulty, keyword, status, pageNo, pageSize),
                AiQuestionBankRespVO.class));
    }
}
