package cn.iocoder.yudao.module.ai.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.activity.AppAiActivityRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserActivityDO;
import cn.iocoder.yudao.module.ai.service.social.AiUserActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - AI 动态")
@RestController
@RequestMapping("/ai/social/activity")
@Validated
public class AppAiActivityController {

    @Resource
    private AiUserActivityService userActivityService;

    @GetMapping("/feed")
    @Operation(summary = "获取动态 Feed 流")
    @Parameter(name = "pageNo", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页条数", required = true, example = "10")
    public CommonResult<PageResult<AppAiActivityRespVO>> getActivityFeed(
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        PageResult<AiUserActivityDO> pageResult = userActivityService.getActivityFeed(
                getLoginUserId(), pageNo, pageSize);
        return success(BeanUtils.toBean(pageResult, AppAiActivityRespVO.class));
    }
}
