package cn.iocoder.yudao.module.ai.controller.app.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 提交作业 Request VO")
@Data
public class AppHomeworkSubmitReqVO {

    @Schema(description = "作业编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "作业编号不能为空")
    private Long homeworkId;

    @Schema(description = "答案", requiredMode = Schema.RequiredMode.REQUIRED, example = "A\n正确\n...")
    @NotEmpty(message = "答案不能为空")
    private String answers;

    @Schema(description = "用时（秒）", example = "120")
    private Integer durationSeconds;

}
