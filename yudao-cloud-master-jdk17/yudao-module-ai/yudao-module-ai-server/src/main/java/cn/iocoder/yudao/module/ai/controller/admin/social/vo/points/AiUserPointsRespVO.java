package cn.iocoder.yudao.module.ai.controller.admin.social.vo.points;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 用户积分响应 VO")
@Data
public class AiUserPointsRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

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

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
