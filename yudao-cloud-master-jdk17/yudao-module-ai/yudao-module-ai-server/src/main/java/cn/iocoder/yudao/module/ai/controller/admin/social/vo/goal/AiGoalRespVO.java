package cn.iocoder.yudao.module.ai.controller.admin.social.vo.goal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学习目标响应 VO")
@Data
public class AiGoalRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "目标类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "DAILY")
    private String goalType;

    @Schema(description = "标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "每天学习英语30分钟")
    private String title;

    @Schema(description = "描述", example = "通过持续学习提升英语水平")
    private String description;

    @Schema(description = "目标值", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer targetValue;

    @Schema(description = "当前值", requiredMode = Schema.RequiredMode.REQUIRED, example = "30")
    private Integer currentValue;

    @Schema(description = "截止时间")
    private LocalDateTime deadline;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "ACTIVE")
    private String status;

    @Schema(description = "AI 反馈", example = "继续保持，你可以的！")
    private String aiFeedback;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
