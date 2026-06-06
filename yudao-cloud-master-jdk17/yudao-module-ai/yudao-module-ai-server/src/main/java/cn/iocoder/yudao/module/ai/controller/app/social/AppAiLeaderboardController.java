package cn.iocoder.yudao.module.ai.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.service.social.AiLeaderboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - AI 排行榜")
@RestController
@RequestMapping("/ai/social/leaderboard")
@Validated
public class AppAiLeaderboardController {

    @Resource
    private AiLeaderboardService leaderboardService;

    @GetMapping("/list")
    @Operation(summary = "获取排行榜")
    @Parameter(name = "periodType", description = "周期类型", required = true, example = "WEEKLY")
    @Parameter(name = "topN", description = "前 N 名", required = true, example = "10")
    public CommonResult<List<Map<String, Object>>> getLeaderboard(
            @RequestParam("periodType") String periodType,
            @RequestParam(value = "topN", defaultValue = "10") Integer topN) {
        return success(leaderboardService.getLeaderboard(periodType, topN));
    }

    @GetMapping("/my-rank")
    @Operation(summary = "获取我的排名")
    @Parameter(name = "periodType", description = "周期类型", required = true, example = "WEEKLY")
    public CommonResult<Map<String, Object>> getMyRank(
            @RequestParam("periodType") String periodType) {
        return success(leaderboardService.getMyRank(getLoginUserId(), periodType));
    }
}
