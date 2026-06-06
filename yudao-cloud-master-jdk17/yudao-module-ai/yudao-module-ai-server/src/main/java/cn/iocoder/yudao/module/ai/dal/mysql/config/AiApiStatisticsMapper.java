package cn.iocoder.yudao.module.ai.dal.mysql.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics.AiStatisticsPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiStatisticsDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI API 统计 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiApiStatisticsMapper extends BaseMapperX<AiApiStatisticsDO> {

    default PageResult<AiApiStatisticsDO> selectPage(AiStatisticsPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiApiStatisticsDO>()
                .eqIfPresent(AiApiStatisticsDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiApiStatisticsDO::getModelId, reqVO.getModelId())
                .eqIfPresent(AiApiStatisticsDO::getApiType, reqVO.getApiType())
                .eqIfPresent(AiApiStatisticsDO::getSuccess, reqVO.getSuccess())
                .betweenIfPresent(AiApiStatisticsDO::getStatDate, reqVO.getStatDateStart(), reqVO.getStatDateEnd())
                .orderByDesc(AiApiStatisticsDO::getId));
    }

    /**
     * 查询今日调用次数
     */
    @Select("SELECT COALESCE(SUM(call_count), 0) FROM ai_api_statistics WHERE stat_date = #{statDate}")
    long selectCallCountByDate(@Param("statDate") LocalDate statDate);

    /**
     * 查询今日去重用户数
     */
    @Select("SELECT COUNT(DISTINCT user_id) FROM ai_api_statistics WHERE stat_date = #{statDate}")
    long selectDistinctUserCountByDate(@Param("statDate") LocalDate statDate);

    /**
     * 查询今日输入 Tokens
     */
    @Select("SELECT COALESCE(SUM(input_tokens), 0) FROM ai_api_statistics WHERE stat_date = #{statDate}")
    long selectInputTokensSumByDate(@Param("statDate") LocalDate statDate);

    /**
     * 查询今日输出 Tokens
     */
    @Select("SELECT COALESCE(SUM(output_tokens), 0) FROM ai_api_statistics WHERE stat_date = #{statDate}")
    long selectOutputTokensSumByDate(@Param("statDate") LocalDate statDate);

    /**
     * 查询每日统计汇总
     */
    @Select("SELECT stat_date AS statDate, SUM(call_count) AS callCount, SUM(input_tokens) AS inputTokens, " +
            "SUM(output_tokens) AS outputTokens, SUM(total_tokens) AS totalTokens, " +
            "SUM(duration_ms) AS durationMs " +
            "FROM ai_api_statistics " +
            "WHERE stat_date BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY stat_date ORDER BY stat_date ASC")
    List<Map<String, Object>> selectDailyStats(@Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * 查询模型使用量统计
     */
    @Select("SELECT model_id AS modelId, model_name AS modelName, platform, SUM(call_count) AS callCount, " +
            "SUM(input_tokens) AS inputTokens, SUM(output_tokens) AS outputTokens, " +
            "SUM(total_tokens) AS totalTokens " +
            "FROM ai_api_statistics " +
            "GROUP BY model_id, model_name, platform ORDER BY callCount DESC")
    List<Map<String, Object>> selectModelUsageStats();

    /**
     * 查询用户活跃度统计
     */
    @Select("SELECT user_id AS userId, COUNT(*) AS callCount, SUM(input_tokens) AS inputTokens, " +
            "SUM(output_tokens) AS outputTokens, SUM(total_tokens) AS totalTokens " +
            "FROM ai_api_statistics " +
            "GROUP BY user_id ORDER BY callCount DESC")
    List<Map<String, Object>> selectUserActivityStats();

}
