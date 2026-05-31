package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathGenerateReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathPageReqVO;
import cn.iocoder.yudao.module.ai.controller.app.education.vo.AppLearningPathRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiLearningPathService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 学习路径")
@RestController
@RequestMapping("/ai/education/path")
public class AppLearningPathController {

    @Resource
    private AiLearningPathService learningPathService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "生成学习路径")
    public Flux<CommonResult<String>> generatePath(@RequestBody @Valid LearningPathGenerateReqVO reqVO) {
        return learningPathService.generatePath(reqVO, getLoginUserId());
    }

    @GetMapping("/page")
    @Operation(summary = "获得路径分页")
    public CommonResult<PageResult<AppLearningPathRespVO>> getPathPage(@Valid LearningPathPageReqVO reqVO) {
        return success(BeanUtils.toBean(learningPathService.getPathPage(reqVO), AppLearningPathRespVO.class));
    }
}
