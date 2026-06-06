package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 敏感词 Response VO")
@Data
public class AiSensitiveWordRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "敏感词", example = "敏感词")
    private String word;

    @Schema(description = "敏感等级", example = "1")
    private Integer level;

    @Schema(description = "分类", example = "政治")
    private String category;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
