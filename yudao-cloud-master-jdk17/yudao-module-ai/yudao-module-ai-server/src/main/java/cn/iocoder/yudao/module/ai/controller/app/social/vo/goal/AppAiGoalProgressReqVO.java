package cn.iocoder.yudao.module.ai.controller.app.social.vo.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 学习目标进度更新 Request VO")
@Data
public class AppAiGoalProgressReqVO {

    @Schema(description = "当前值", requiredMode = Schema.RequiredMode.REQUIRED, example = "50")
    @NotNull(message = "当前值不能为空")
    private Integer currentValue;

}
