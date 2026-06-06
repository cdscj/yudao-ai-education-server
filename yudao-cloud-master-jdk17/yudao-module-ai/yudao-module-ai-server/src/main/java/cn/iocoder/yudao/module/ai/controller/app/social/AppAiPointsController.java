package cn.iocoder.yudao.module.ai.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.points.AppAiUserPointsRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserPointsDO;
import cn.iocoder.yudao.module.ai.service.social.AiUserPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - AI 积分")
@RestController
@RequestMapping("/ai/social/points")
@Validated
public class AppAiPointsController {

    @Resource
    private AiUserPointsService userPointsService;

    @GetMapping("/my")
    @Operation(summary = "获取我的积分")
    public CommonResult<AppAiUserPointsRespVO> getMyPoints() {
        AiUserPointsDO userPoints = userPointsService.getUserPoints(getLoginUserId());
        return success(BeanUtils.toBean(userPoints, AppAiUserPointsRespVO.class));
    }

    @GetMapping("/level")
    @Operation(summary = "获取我的等级")
    public CommonResult<Map<String, Object>> getUserLevel() {
        return success(userPointsService.getUserLevel(getLoginUserId()));
    }
}
