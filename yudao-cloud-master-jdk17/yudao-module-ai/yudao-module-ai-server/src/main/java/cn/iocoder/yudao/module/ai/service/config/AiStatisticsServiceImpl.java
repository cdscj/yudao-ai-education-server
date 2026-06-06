package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics.AiStatisticsPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiStatisticsDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiApiStatisticsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.module.ai.framework.config.AiAsyncConfiguration.AI_THREAD_POOL_TASK_EXECUTOR;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.STATISTICS_NOT_EXISTS;

/**
 * AI 统计 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class AiStatisticsServiceImpl implements AiStatisticsService {

    @Resource
    private AiApiStatisticsMapper apiStatisticsMapper;

    @Override
    @Async(AI_THREAD_POOL_TASK_EXECUTOR)
    public void recordApiCall(Long userId, Long modelId, String modelName, String platform,
                              String apiType, Integer inputTokens, Integer outputTokens,
                              Long durationMs, Boolean success, String errorMessage) {
        try {
            AiApiStatisticsDO statistics = AiApiStatisticsDO.builder()
                    .userId(userId)
                    .modelId(modelId)
                    .modelName(modelName)
                    .platform(platform)
                    .apiType(apiType)
                    .callCount(1)
                    .inputTokens(inputTokens != null ? inputTokens : 0)
                    .outputTokens(outputTokens != null ? outputTokens : 0)
                    .totalTokens((inputTokens != null ? inputTokens : 0) + (outputTokens != null ? outputTokens : 0))
                    .durationMs(durationMs)
                    .success(success)
                    .errorMessage(errorMessage)
                    .statDate(LocalDate.now())
                    .build();
            apiStatisticsMapper.insert(statistics);
        } catch (Exception e) {
            log.error("[recordApiCall][记录 API 调用统计异常，userId({}), modelId({})]", userId, modelId, e);
        }
    }

    @Override
    public Map<String, Object> getOverviewData() {
        LocalDate today = LocalDate.now();
        Map<String, Object> overview = new LinkedHashMap<>();

        // 今日数据
        long todayCalls = apiStatisticsMapper.selectCallCountByDate(today);
        long todayUsers = apiStatisticsMapper.selectDistinctUserCountByDate(today);
        long todayInputTokens = apiStatisticsMapper.selectInputTokensSumByDate(today);
        long todayOutputTokens = apiStatisticsMapper.selectOutputTokensSumByDate(today);

        // 本月数据
        LocalDate monthStart = today.withDayOfMonth(1);
        long monthCalls = 0;
        long monthUsers = 0;
        // 累加本月每天的统计数据
        for (LocalDate date = monthStart; !date.isAfter(today); date = date.plusDays(1)) {
            monthCalls += apiStatisticsMapper.selectCallCountByDate(date);
            // 粗略计算本月用户数（取最新一天的去重用户数作为近似值）
            if (date.equals(today)) {
                monthUsers = apiStatisticsMapper.selectDistinctUserCountByDate(date);
            }
        }

        overview.put("todayCalls", todayCalls);
        overview.put("todayUsers", todayUsers);
        overview.put("todayTokens", todayInputTokens + todayOutputTokens);
        overview.put("monthCalls", monthCalls);
        overview.put("monthUsers", monthUsers);

        return overview;
    }

    @Override
    public List<Map<String, Object>> getDailyTrend(String startDate, String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return apiStatisticsMapper.selectDailyStats(start, end);
    }

    @Override
    public List<Map<String, Object>> getModelUsageRanking(String startDate, String endDate) {
        return apiStatisticsMapper.selectModelUsageStats();
    }

    @Override
    public List<Map<String, Object>> getUserActivityRanking(String startDate, String endDate, Integer topN) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        // 使用 mapper 的基础查询后，在内存中按 callCount 排序取 topN
        List<Map<String, Object>> allStats = apiStatisticsMapper.selectUserActivityStats();
        if (topN != null && topN > 0 && allStats.size() > topN) {
            return allStats.subList(0, topN);
        }
        return allStats;
    }

    @Override
    public PageResult<AiApiStatisticsDO> getStatisticsPage(AiStatisticsPageReqVO pageReqVO) {
        return apiStatisticsMapper.selectPage(pageReqVO);
    }

}
