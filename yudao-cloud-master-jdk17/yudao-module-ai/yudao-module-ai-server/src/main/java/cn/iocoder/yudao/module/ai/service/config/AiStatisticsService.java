package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics.AiStatisticsPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiStatisticsDO;

import java.util.List;
import java.util.Map;

/**
 * AI 统计 Service 接口
 *
 * @author 芋道源码
 */
public interface AiStatisticsService {

    /**
     * 记录 API 调用
     *
     * @param userId       用户编号
     * @param modelId      模型编号
     * @param modelName    模型名称
     * @param platform     平台
     * @param apiType      API 类型
     * @param inputTokens  输入 Token 数
     * @param outputTokens 输出 Token 数
     * @param durationMs   耗时（毫秒）
     * @param success      是否成功
     * @param errorMessage 错误信息
     */
    void recordApiCall(Long userId, Long modelId, String modelName, String platform,
                       String apiType, Integer inputTokens, Integer outputTokens,
                       Long durationMs, Boolean success, String errorMessage);

    /**
     * 获取概览数据
     *
     * @return 概览数据（今日调用次数、今日用户数、今日 Token 数、本月调用次数、本月用户数）
     */
    Map<String, Object> getOverviewData();

    /**
     * 获取每日趋势
     *
     * @param startDate 开始日期（yyyy-MM-dd）
     * @param endDate   结束日期（yyyy-MM-dd）
     * @return 每日调用趋势
     */
    List<Map<String, Object>> getDailyTrend(String startDate, String endDate);

    /**
     * 获取模型使用排名
     *
     * @param startDate 开始日期（yyyy-MM-dd）
     * @param endDate   结束日期（yyyy-MM-dd）
     * @return 模型使用排名
     */
    List<Map<String, Object>> getModelUsageRanking(String startDate, String endDate);

    /**
     * 获取用户活跃度排名
     *
     * @param startDate 开始日期（yyyy-MM-dd）
     * @param endDate   结束日期（yyyy-MM-dd）
     * @param topN      返回前 N 条
     * @return 用户活跃度排名
     */
    List<Map<String, Object>> getUserActivityRanking(String startDate, String endDate, Integer topN);

    /**
     * 获得统计分页
     *
     * @param pageReqVO 分页查询
     * @return 统计分页
     */
    PageResult<AiApiStatisticsDO> getStatisticsPage(AiStatisticsPageReqVO pageReqVO);

}
