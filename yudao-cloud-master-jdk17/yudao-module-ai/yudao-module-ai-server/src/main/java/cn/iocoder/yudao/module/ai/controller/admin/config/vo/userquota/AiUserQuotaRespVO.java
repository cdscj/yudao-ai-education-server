package cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 用户配额 Response VO")
@Data
public class AiUserQuotaRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "配额类型", example = "1")
    private Integer quotaType;

    @Schema(description = "每日限额", example = "100")
    private Long dailyLimit;

    @Schema(description = "每月限额", example = "3000")
    private Long monthlyLimit;

    @Schema(description = "总限额", example = "100000")
    private Long totalLimit;

    @Schema(description = "每日已用", example = "50")
    private Long dailyUsed;

    @Schema(description = "每月已用", example = "1500")
    private Long monthlyUsed;

    @Schema(description = "总已用", example = "50000")
    private Long totalUsed;

    @Schema(description = "最后重置日期", example = "2024-01-01")
    private LocalDate lastResetDate;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
