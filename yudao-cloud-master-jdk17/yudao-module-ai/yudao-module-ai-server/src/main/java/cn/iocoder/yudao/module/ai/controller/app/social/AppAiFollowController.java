package cn.iocoder.yudao.module.ai.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.follow.AppAiFollowRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFollowDO;
import cn.iocoder.yudao.module.ai.service.social.AiFollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - AI 关注")
@RestController
@RequestMapping("/ai/social/follow")
@Validated
public class AppAiFollowController {

    @Resource
    private AiFollowService followService;

    @PostMapping("/follow")
    @Operation(summary = "关注用户")
    @Parameter(name = "followUserId", description = "被关注用户编号", required = true, example = "2")
    public CommonResult<Boolean> follow(@RequestParam("followUserId") Long followUserId) {
        followService.follow(getLoginUserId(), followUserId);
        return success(true);
    }

    @PostMapping("/unfollow")
    @Operation(summary = "取消关注")
    @Parameter(name = "followUserId", description = "被关注用户编号", required = true, example = "2")
    public CommonResult<Boolean> unfollow(@RequestParam("followUserId") Long followUserId) {
        followService.unfollow(getLoginUserId(), followUserId);
        return success(true);
    }

    @GetMapping("/following")
    @Operation(summary = "获取关注列表")
    @Parameter(name = "pageNo", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页条数", required = true, example = "10")
    public CommonResult<List<AppAiFollowRespVO>> getFollowingList(
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        List<AiFollowDO> list = followService.getFollowingList(getLoginUserId(), pageNo, pageSize);
        return success(BeanUtils.toBean(list, AppAiFollowRespVO.class));
    }

    @GetMapping("/followers")
    @Operation(summary = "获取粉丝列表")
    @Parameter(name = "pageNo", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页条数", required = true, example = "10")
    public CommonResult<List<AppAiFollowRespVO>> getFollowerList(
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        List<AiFollowDO> list = followService.getFollowerList(getLoginUserId(), pageNo, pageSize);
        return success(BeanUtils.toBean(list, AppAiFollowRespVO.class));
    }

    @GetMapping("/is-following")
    @Operation(summary = "是否已关注")
    @Parameter(name = "targetUserId", description = "目标用户编号", required = true, example = "2")
    public CommonResult<Boolean> isFollowing(@RequestParam("targetUserId") Long targetUserId) {
        return success(followService.isFollowing(getLoginUserId(), targetUserId));
    }
}
