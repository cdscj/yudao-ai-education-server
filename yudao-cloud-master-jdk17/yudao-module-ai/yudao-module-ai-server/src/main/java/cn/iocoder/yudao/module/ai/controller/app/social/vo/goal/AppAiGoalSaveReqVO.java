package cn.iocoder.yudao.module.ai.controller.app.social.vo.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "用户 App - 学习目标保存 Request VO")
@Data
public class AppAiGoalSaveReqVO {

    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "DAILY")
    @NotEmpty(message = "目标类型不能为空")
    private String goalType;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "每天学习英语30分钟")
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Schema(description = "描述", example = "通过持续学习提升英语水平")
    private String description;

    @Schema(description = "目标值", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "目标值不能为空")
    private Integer targetValue;

    @Schema(description = "截止时间")
    private LocalDate deadline;

}
