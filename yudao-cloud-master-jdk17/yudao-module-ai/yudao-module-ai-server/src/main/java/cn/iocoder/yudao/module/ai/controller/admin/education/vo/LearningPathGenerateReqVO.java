package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 学习路径生成 Request VO")
@Data
public class LearningPathGenerateReqVO {
    @Schema(description = "学习目标", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "学习目标不能为空")
    private String goal;
    @Schema(description = "课程/领域", example = "数据结构与算法")
    private String courseName;
    @Schema(description = "时间周期(天)")
    private Integer durationDays;
}
