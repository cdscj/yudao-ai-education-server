package cn.iocoder.yudao.module.ai.controller.app.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.time.LocalDate;

@Schema(description = "用户 App - 创建学习计划 Request VO")
@Data
public class AppStudyPlanCreateReqVO {

    @Schema(description = "计划标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "Java 学习计划")
    @NotEmpty(message = "标题不能为空")
    private String title;

    @Schema(description = "计划类型", example = "WEEKLY")
    private String planType;

    @Schema(description = "开始日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06-05")
    @NotEmpty(message = "开始日期不能为空")
    private String startDate;

    @Schema(description = "结束日期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2026-06-11")
    @NotEmpty(message = "结束日期不能为空")
    private String endDate;

    @Schema(description = "学习目标", example = "掌握 Java 基础")
    private String goal;

    @Schema(description = "计划描述", example = "每天学习2小时")
    private String description;

}
