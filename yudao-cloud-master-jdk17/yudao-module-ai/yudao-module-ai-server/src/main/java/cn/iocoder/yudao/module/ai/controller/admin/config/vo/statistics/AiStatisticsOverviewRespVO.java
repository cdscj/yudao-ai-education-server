package cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - AI 统计概览 Response VO")
@Data
public class AiStatisticsOverviewRespVO {

    @Schema(description = "今日调用次数", example = "100")
    private Long todayCalls;

    @Schema(description = "今日活跃用户数", example = "50")
    private Long todayUsers;

    @Schema(description = "今日消耗 Token 数", example = "50000")
    private Long todayTokens;

    @Schema(description = "本月调用次数", example = "3000")
    private Long monthCalls;

    @Schema(description = "本月活跃用户数", example = "500")
    private Long monthUsers;

    @Schema(description = "本月消耗 Token 数", example = "1500000")
    private Long monthTokens;

    @Schema(description = "总调用次数", example = "100000")
    private Long totalCalls;

    @Schema(description = "总用户数", example = "2000")
    private Long totalUsers;

    @Schema(description = "今日趋势")
    private List<TrendItem> todayTrend;

    @Schema(description = "模型使用排行")
    private List<ModelRankItem> modelUsageRanking;

    @Schema(description = "用户活跃排行")
    private List<UserRankItem> userActivityRanking;

    @Schema(description = "今日趋势条目")
    @Data
    public static class TrendItem {

        @Schema(description = "日期", example = "2024-01-01 10:00")
        private String date;

        @Schema(description = "数量", example = "10")
        private Long count;

    }

    @Schema(description = "模型排行条目")
    @Data
    public static class ModelRankItem {

        @Schema(description = "模型名称", example = "GPT-3.5")
        private String modelName;

        @Schema(description = "模型平台", example = "OpenAI")
        private String platform;

        @Schema(description = "调用次数", example = "1000")
        private Long callCount;

        @Schema(description = "总 Token 数", example = "50000")
        private Long totalTokens;

    }

    @Schema(description = "用户排行条目")
    @Data
    public static class UserRankItem {

        @Schema(description = "用户编号", example = "1")
        private Long userId;

        @Schema(description = "调用次数", example = "500")
        private Long callCount;

        @Schema(description = "总 Token 数", example = "25000")
        private Long totalTokens;

    }

}
