package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 画像对话构建 Request VO")
@Data
public class StudentProfileChatReqVO {
    @Schema(description = "用户消息内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "消息内容不能为空")
    private String message;
}
