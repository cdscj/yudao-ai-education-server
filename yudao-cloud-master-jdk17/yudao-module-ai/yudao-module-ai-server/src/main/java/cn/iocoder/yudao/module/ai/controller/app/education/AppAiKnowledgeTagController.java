package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiKnowledgeTagRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiKnowledgeTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 App - 知识点标签")
@RestController
@RequestMapping("/ai/education/knowledge-tag")
@Validated
public class AppAiKnowledgeTagController {

    @Resource
    private AiKnowledgeTagService knowledgeTagService;

    @GetMapping("/list-by-subject")
    @Operation(summary = "按学科获得知识点标签列表")
    @Parameter(name = "subjectId", description = "学科编号", required = true, example = "1")
    public CommonResult<List<AiKnowledgeTagRespVO>> listBySubject(@RequestParam("subjectId") Long subjectId) {
        return success(BeanUtils.toBean(knowledgeTagService.getKnowledgeTagListBySubjectId(subjectId),
                AiKnowledgeTagRespVO.class));
    }
}
