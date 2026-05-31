package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "管理后台 - 辅导提问 Request VO")
@Data
public class TutoringChatReqVO {
    @Schema(description = "会话编号", example = "0表示新会话")
    private Long sessionId;
    @Schema(description = "问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "问题不能为空")
    private String question;
}
