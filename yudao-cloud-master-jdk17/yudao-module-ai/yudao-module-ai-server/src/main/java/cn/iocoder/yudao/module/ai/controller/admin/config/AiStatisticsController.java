package cn.iocoder.yudao.module.ai.controller.admin.config;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiStatisticsDO;
import cn.iocoder.yudao.module.ai.service.config.AiStatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 统计")
@RestController
@RequestMapping("/ai/statistics")
@Validated
public class AiStatisticsController {

    @Resource
    private AiStatisticsService statisticsService;

    @GetMapping("/overview")
    @Operation(summary = "获得统计概览数据")
    @PreAuthorize("@ss.hasPermission('ai:statistics:query')")
    public CommonResult<Map<String, Object>> getOverviewData() {
        Map<String, Object> overview = statisticsService.getOverviewData();
        return success(overview);
    }

    @GetMapping("/daily-trend")
    @Operation(summary = "获得每日趋势数据")
    @Parameter(name = "startDate", description = "开始日期", example = "2024-01-01")
    @Parameter(name = "endDate", description = "结束日期", example = "2024-01-31")
    @PreAuthorize("@ss.hasPermission('ai:statistics:query')")
    public CommonResult<List<Map<String, Object>>> getDailyTrend(@RequestParam(value = "startDate", required = false) String startDate,
                                                                 @RequestParam(value = "endDate", required = false) String endDate) {
        return success(statisticsService.getDailyTrend(startDate, endDate));
    }

    @GetMapping("/model-ranking")
    @Operation(summary = "获得模型使用排行")
    @Parameter(name = "startDate", description = "开始日期", example = "2024-01-01")
    @Parameter(name = "endDate", description = "结束日期", example = "2024-01-31")
    @PreAuthorize("@ss.hasPermission('ai:statistics:query')")
    public CommonResult<List<Map<String, Object>>> getModelUsageRanking(@RequestParam(value = "startDate", required = false) String startDate,
                                                                        @RequestParam(value = "endDate", required = false) String endDate) {
        return success(statisticsService.getModelUsageRanking(startDate, endDate));
    }

    @GetMapping("/user-ranking")
    @Operation(summary = "获得用户活跃排行")
    @Parameter(name = "startDate", description = "开始日期", example = "2024-01-01")
    @Parameter(name = "endDate", description = "结束日期", example = "2024-01-31")
    @Parameter(name = "topN", description = "返回前 N 条", example = "10")
    @PreAuthorize("@ss.hasPermission('ai:statistics:query')")
    public CommonResult<List<Map<String, Object>>> getUserActivityRanking(@RequestParam(value = "startDate", required = false) String startDate,
                                                                          @RequestParam(value = "endDate", required = false) String endDate,
                                                                          @RequestParam(value = "topN", required = false) Integer topN) {
        return success(statisticsService.getUserActivityRanking(startDate, endDate, topN));
    }

    @GetMapping("/page")
    @Operation(summary = "获得统计分页")
    @PreAuthorize("@ss.hasPermission('ai:statistics:query')")
    public CommonResult<PageResult<AiStatisticsRespVO>> getStatisticsPage(@Valid AiStatisticsPageReqVO pageReqVO) {
        PageResult<AiApiStatisticsDO> pageResult = statisticsService.getStatisticsPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiStatisticsRespVO.class));
    }

}
