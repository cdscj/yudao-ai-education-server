package cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 统计 Response VO")
@Data
public class AiStatisticsRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "模型编号", example = "1")
    private Long modelId;

    @Schema(description = "模型名称", example = "GPT-3.5")
    private String modelName;

    @Schema(description = "模型平台", example = "OpenAI")
    private String platform;

    @Schema(description = "API 类型", example = "chat")
    private String apiType;

    @Schema(description = "调用次数", example = "100")
    private Integer callCount;

    @Schema(description = "输入 Token 数", example = "1000")
    private Integer inputTokens;

    @Schema(description = "输出 Token 数", example = "2000")
    private Integer outputTokens;

    @Schema(description = "总 Token 数", example = "3000")
    private Integer totalTokens;

    @Schema(description = "耗时(毫秒)", example = "500")
    private Integer durationMs;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "错误信息", example = "连接超时")
    private String errorMessage;

    @Schema(description = "统计日期", example = "2024-01-01")
    private LocalDate statDate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
