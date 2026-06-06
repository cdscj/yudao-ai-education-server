package cn.iocoder.yudao.module.ai.controller.app.social.vo.checkin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 签到摘要 Response VO")
@Data
public class AppAiCheckInSummaryRespVO {

    @Schema(description = "累计签到天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer totalDays;

    @Schema(description = "连续签到天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer streakDays;

    @Schema(description = "今日是否已签到", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean todayChecked;

    @Schema(description = "当前等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer currentLevel;

    @Schema(description = "等级称号", requiredMode = Schema.RequiredMode.REQUIRED, example = "进阶学习者")
    private String rankTitle;

    @Schema(description = "总积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "350")
    private Integer totalPoints;

}
