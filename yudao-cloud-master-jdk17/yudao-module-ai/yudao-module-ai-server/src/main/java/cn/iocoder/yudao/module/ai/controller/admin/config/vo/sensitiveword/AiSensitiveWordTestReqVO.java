package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - AI 敏感词测试 Request VO")
@Data
public class AiSensitiveWordTestReqVO {

    @Schema(description = "待检测文本", requiredMode = Schema.RequiredMode.REQUIRED, example = "这是一段包含敏感词的测试文本")
    @NotEmpty(message = "待检测文本不能为空")
    private String content;

}
