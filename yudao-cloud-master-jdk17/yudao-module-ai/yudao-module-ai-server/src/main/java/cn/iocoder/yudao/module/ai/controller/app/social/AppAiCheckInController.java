package cn.iocoder.yudao.module.ai.controller.app.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.checkin.AppAiCheckInRespVO;
import cn.iocoder.yudao.module.ai.controller.app.social.vo.checkin.AppAiCheckInSummaryRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiCheckInRecordDO;
import cn.iocoder.yudao.module.ai.service.social.AiCheckInService;
import cn.iocoder.yudao.module.ai.service.social.AiUserPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - AI 签到")
@RestController
@RequestMapping("/ai/social/check-in")
@Validated
public class AppAiCheckInController {

    @Resource
    private AiCheckInService checkInService;
    @Resource
    private AiUserPointsService userPointsService;

    @PostMapping("/check-in")
    @Operation(summary = "签到")
    public CommonResult<AppAiCheckInRespVO> checkIn() {
        AiCheckInRecordDO record = checkInService.checkIn(getLoginUserId());
        return success(BeanUtils.toBean(record, AppAiCheckInRespVO.class));
    }

    @GetMapping("/summary")
    @Operation(summary = "获取签到摘要")
    public CommonResult<AppAiCheckInSummaryRespVO> getCheckInSummary() {
        Map<String, Object> summary = checkInService.getCheckInSummary(getLoginUserId());
        Map<String, Object> levelInfo = userPointsService.getUserLevel(getLoginUserId());
        AppAiCheckInSummaryRespVO respVO = new AppAiCheckInSummaryRespVO();
        respVO.setTotalDays((Integer) summary.get("totalDays"));
        respVO.setStreakDays((Integer) summary.get("streakDays"));
        respVO.setTodayChecked((Boolean) summary.get("todayChecked"));
        respVO.setCurrentLevel((Integer) levelInfo.get("level"));
        respVO.setRankTitle((String) levelInfo.get("rankTitle"));
        respVO.setTotalPoints((Integer) levelInfo.get("totalPoints"));
        return success(respVO);
    }

    @GetMapping("/records")
    @Operation(summary = "获取签到记录")
    @Parameter(name = "pageNo", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页条数", required = true, example = "10")
    public CommonResult<List<AppAiCheckInRespVO>> getCheckInRecords(
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        List<AiCheckInRecordDO> records = checkInService.getCheckInRecords(getLoginUserId(), pageNo, pageSize);
        return success(BeanUtils.toBean(records, AppAiCheckInRespVO.class));
    }

    @GetMapping("/calendar")
    @Operation(summary = "获取签到日历")
    @Parameter(name = "yearMonth", description = "年月", required = true, example = "2026-06")
    public CommonResult<List<AppAiCheckInRespVO>> getCheckInCalendar(
            @RequestParam("yearMonth") String yearMonth) {
        List<AiCheckInRecordDO> records = checkInService.getCheckInCalendar(getLoginUserId(), yearMonth);
        return success(BeanUtils.toBean(records, AppAiCheckInRespVO.class));
    }
}
