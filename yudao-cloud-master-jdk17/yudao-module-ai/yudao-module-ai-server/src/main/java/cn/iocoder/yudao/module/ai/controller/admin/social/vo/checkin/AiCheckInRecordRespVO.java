package cn.iocoder.yudao.module.ai.controller.admin.social.vo.checkin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 签到记录响应 VO")
@Data
public class AiCheckInRecordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "签到日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate checkInDate;

    @Schema(description = "连续签到天数")
    private Integer streakDays;

    @Schema(description = "总签到天数")
    private Integer totalDays;

    @Schema(description = "签到获得积分")
    private Integer pointsEarned;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
