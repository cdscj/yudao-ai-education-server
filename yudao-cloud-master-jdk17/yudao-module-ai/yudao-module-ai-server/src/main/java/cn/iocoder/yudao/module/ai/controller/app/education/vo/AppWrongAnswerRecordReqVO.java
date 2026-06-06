package cn.iocoder.yudao.module.ai.controller.app.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 记录答题 Request VO")
@Data
public class AppWrongAnswerRecordReqVO {

    @Schema(description = "题目编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "题目编号不能为空")
    private Long questionId;

    @Schema(description = "学科编号", example = "1")
    private Long subjectId;

    @Schema(description = "用户答案", requiredMode = Schema.RequiredMode.REQUIRED, example = "A")
    @NotEmpty(message = "用户答案不能为空")
    private String userAnswer;

    @Schema(description = "正确答案", requiredMode = Schema.RequiredMode.REQUIRED, example = "B")
    @NotEmpty(message = "正确答案不能为空")
    private String correctAnswer;

    @Schema(description = "是否正确", example = "false")
    private Boolean isCorrect;

    @Schema(description = "知识点标签", example = "1,2")
    private String knowledgeTagIds;

    @Schema(description = "错题来源类型", example = "HOMEWORK")
    private String sourceType;

    @Schema(description = "来源编号", example = "1")
    private Long sourceId;

}
