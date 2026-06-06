package cn.iocoder.yudao.module.ai.controller.app.social.vo.points;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 用户积分 Response VO")
@Data
public class AppAiUserPointsRespVO {

    @Schema(description = "总积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "350")
    private Integer totalPoints;

    @Schema(description = "本周积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    private Integer weeklyPoints;

    @Schema(description = "本月积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "120")
    private Integer monthlyPoints;

    @Schema(description = "等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "3")
    private Integer level;

    @Schema(description = "等级称号", requiredMode = Schema.RequiredMode.REQUIRED, example = "进阶学习者")
    private String rankTitle;

}
