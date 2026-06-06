package cn.iocoder.yudao.module.ai.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.checkin.AiCheckInRecordPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.checkin.AiCheckInRecordRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiCheckInRecordDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiCheckInRecordMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 签到记录")
@RestController
@RequestMapping("/ai/social/check-in-record")
@Validated
@Slf4j
public class AiCheckInRecordController {

    @Resource
    private AiCheckInRecordMapper checkInRecordMapper;

    @GetMapping("/page")
    @Operation(summary = "获得签到记录分页")
    @PreAuthorize("@ss.hasPermission('ai:social:check-in-query')")
    public CommonResult<PageResult<AiCheckInRecordRespVO>> getCheckInRecordPage(
            @Valid AiCheckInRecordPageReqVO pageReqVO) {
        PageResult<AiCheckInRecordDO> pageResult = checkInRecordMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<AiCheckInRecordDO>()
                        .eqIfPresent(AiCheckInRecordDO::getUserId, pageReqVO.getUserId())
                        .geIfPresent(AiCheckInRecordDO::getCheckInDate, pageReqVO.getCheckInDateStart())
                        .leIfPresent(AiCheckInRecordDO::getCheckInDate, pageReqVO.getCheckInDateEnd())
                        .orderByDesc(AiCheckInRecordDO::getId));
        return success(BeanUtils.toBean(pageResult, AiCheckInRecordRespVO.class));
    }

    @GetMapping("/stats")
    @Operation(summary = "获得签到统计")
    @PreAuthorize("@ss.hasPermission('ai:social:check-in-query')")
    public CommonResult<CheckInStatsRespVO> getCheckInStats() {
        LocalDate today = LocalDate.now();

        // 今日签到次数
        Long todayCount = checkInRecordMapper.selectCount(
                AiCheckInRecordDO::getCheckInDate, today);

        // 签到总用户数
        List<AiCheckInRecordDO> allRecords = checkInRecordMapper.selectList();
        Long totalUsers = allRecords.stream()
                .map(AiCheckInRecordDO::getUserId)
                .distinct()
                .count();

        // 平均连续签到天数
        Double avgStreakDays = allRecords.stream()
                .filter(r -> r.getStreakDays() != null)
                .collect(Collectors.averagingInt(AiCheckInRecordDO::getStreakDays));

        return success(new CheckInStatsRespVO(todayCount, totalUsers, avgStreakDays));
    }

    @Data
    static class CheckInStatsRespVO {

        private Long todayCount;
        private Long totalUsers;
        private Double avgStreakDays;

        CheckInStatsRespVO(Long todayCount, Long totalUsers, Double avgStreakDays) {
            this.todayCount = todayCount;
            this.totalUsers = totalUsers;
            this.avgStreakDays = avgStreakDays;
        }

    }

}
