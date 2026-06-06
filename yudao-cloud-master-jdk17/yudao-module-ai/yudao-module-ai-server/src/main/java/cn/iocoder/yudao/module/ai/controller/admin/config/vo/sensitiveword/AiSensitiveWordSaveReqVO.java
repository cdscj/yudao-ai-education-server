package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - AI 敏感词新增/修改 Request VO")
@Data
public class AiSensitiveWordSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "敏感词", requiredMode = Schema.RequiredMode.REQUIRED, example = "敏感词")
    @NotEmpty(message = "敏感词不能为空")
    private String word;

    @Schema(description = "敏感等级", example = "1")
    private Integer level;

    @Schema(description = "分类", example = "政治")
    private String category;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
