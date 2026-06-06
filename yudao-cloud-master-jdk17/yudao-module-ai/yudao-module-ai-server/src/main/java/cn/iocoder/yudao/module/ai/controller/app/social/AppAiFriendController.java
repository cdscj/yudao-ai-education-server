package cn.iocoder.yudao.module.ai.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.friend.AppAiFriendApplyReqVO;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.friend.AppAiFriendRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFriendDO;
import cn.iocoder.yudao.module.ai.service.social.AiFriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - AI 好友")
@RestController
@RequestMapping("/ai/social/friend")
@Validated
public class AppAiFriendController {

    @Resource
    private AiFriendService friendService;

    @PostMapping("/send-request")
    @Operation(summary = "发送好友申请")
    public CommonResult<Boolean> sendFriendRequest(@Valid @RequestBody AppAiFriendApplyReqVO reqVO) {
        friendService.sendFriendRequest(getLoginUserId(), reqVO.getFriendUserId(), reqVO.getRemark());
        return success(true);
    }

    @PostMapping("/accept")
    @Operation(summary = "接受好友申请")
    @Parameter(name = "requestId", description = "申请编号", required = true, example = "1")
    public CommonResult<Boolean> acceptFriendRequest(@RequestParam("requestId") Long requestId) {
        friendService.acceptFriendRequest(requestId, getLoginUserId());
        return success(true);
    }

    @PostMapping("/reject")
    @Operation(summary = "拒绝好友申请")
    @Parameter(name = "requestId", description = "申请编号", required = true, example = "1")
    public CommonResult<Boolean> rejectFriendRequest(@RequestParam("requestId") Long requestId) {
        friendService.rejectFriendRequest(requestId, getLoginUserId());
        return success(true);
    }

    @DeleteMapping("/remove")
    @Operation(summary = "删除好友")
    @Parameter(name = "friendUserId", description = "好友用户编号", required = true, example = "2")
    public CommonResult<Boolean> removeFriend(@RequestParam("friendUserId") Long friendUserId) {
        friendService.removeFriend(getLoginUserId(), friendUserId);
        return success(true);
    }

    @GetMapping("/list")
    @Operation(summary = "获取好友列表")
    public CommonResult<List<AppAiFriendRespVO>> getFriendList() {
        List<AiFriendDO> list = friendService.getFriendList(getLoginUserId(), 1, 100);
        return success(BeanUtils.toBean(list, AppAiFriendRespVO.class));
    }

    @GetMapping("/pending")
    @Operation(summary = "获取待处理的申请")
    public CommonResult<List<AppAiFriendRespVO>> getPendingRequests() {
        List<AiFriendDO> list = friendService.getPendingRequests(getLoginUserId());
        return success(BeanUtils.toBean(list, AppAiFriendRespVO.class));
    }
}
