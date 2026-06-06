package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourcePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourceGenerateReqVO;
import cn.iocoder.yudao.module.ai.controller.app.education.vo.AppLearningResourceRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningResourceDO;
import cn.iocoder.yudao.module.ai.service.education.AiLearningResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 学习资源")
@RestController
@RequestMapping("/ai/education/resource")
public class AppLearningResourceController {

    @Resource
    private AiLearningResourceService learningResourceService;

    @PostMapping(value = "/generate-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "生成学习资源")
    public Flux<CommonResult<String>> generateResource(@RequestBody @Valid LearningResourceGenerateReqVO reqVO) {
        return learningResourceService.generateResource(reqVO, getLoginUserId());
    }

    @GetMapping("/page")
    @Operation(summary = "获得我的资源分页")
    public CommonResult<PageResult<AppLearningResourceRespVO>> getResourcePage(@Valid LearningResourcePageReqVO reqVO) {
        reqVO.setUserId(getLoginUserId());
        return success(BeanUtils.toBean(learningResourceService.getResourcePage(reqVO), AppLearningResourceRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获取资源详情")
    @Parameter(name = "id", required = true)
    public CommonResult<AppLearningResourceRespVO> getResource(@RequestParam("id") Long id) {
        return success(BeanUtils.toBean(learningResourceService.getResource(id), AppLearningResourceRespVO.class));
    }
}
