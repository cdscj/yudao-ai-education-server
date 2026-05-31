package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 学习资源生成 Request VO")
@Data
public class LearningResourceGenerateReqVO {
    @Schema(description = "资源类型", example = "DOCUMENT")
    @NotBlank(message = "资源类型不能为空")
    private String resourceType;
    @Schema(description = "主题/课程", example = "机器学习基础")
    @NotBlank(message = "主题不能为空")
    private String topic;
    @Schema(description = "难度", example = "BEGINNER")
    private String difficulty;
    @Schema(description = "额外要求")
    private String requirements;
}
