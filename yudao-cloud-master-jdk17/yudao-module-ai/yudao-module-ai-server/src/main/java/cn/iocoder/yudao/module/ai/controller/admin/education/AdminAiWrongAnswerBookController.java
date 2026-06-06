package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiWrongAnswerBookRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiWrongAnswerBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 错题本")
@RestController
@RequestMapping("/ai/education/wrong-answer")
@Validated
public class AdminAiWrongAnswerBookController {

    @Resource
    private AiWrongAnswerBookService wrongAnswerBookService;

    @GetMapping("/page")
    @Operation(summary = "获得错题本分页")
    @PreAuthorize("@ss.hasPermission('ai:wrong-answer:query')")
    public CommonResult<PageResult<AiWrongAnswerBookRespVO>> page(
            @RequestParam(value = "userId", required = false) Long userId,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "masteryLevel", required = false) Integer masteryLevel,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        return success(BeanUtils.toBean(wrongAnswerBookService.getWrongAnswerPage(
                userId, subjectId, masteryLevel, pageNo, pageSize), AiWrongAnswerBookRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得错题")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:wrong-answer:query')")
    public CommonResult<AiWrongAnswerBookRespVO> get(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(wrongAnswerBookService.getWrongAnswer(id), AiWrongAnswerBookRespVO.class));
    }

    @GetMapping("/stats")
    @Operation(summary = "获得错题统计")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:wrong-answer:query')")
    public CommonResult<Map<String, Object>> stats(@RequestParam("userId") Long userId) {
        return success(wrongAnswerBookService.getStats(userId));
    }
}
