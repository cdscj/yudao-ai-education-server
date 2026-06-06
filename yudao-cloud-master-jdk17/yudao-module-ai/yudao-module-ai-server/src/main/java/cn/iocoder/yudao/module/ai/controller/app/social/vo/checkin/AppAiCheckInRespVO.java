package cn.iocoder.yudao.module.ai.controller.app.social.vo.checkin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "用户 App - 签到 Response VO")
@Data
public class AppAiCheckInRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "签到日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate checkInDate;

    @Schema(description = "连续签到天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "5")
    private Integer streakDays;

    @Schema(description = "累计签到天数", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer totalDays;

    @Schema(description = "获得积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private Integer pointsEarned;

    @Schema(description = "AI 鼓励语", requiredMode = Schema.RequiredMode.REQUIRED)
    private String aiEncouragement;

}
