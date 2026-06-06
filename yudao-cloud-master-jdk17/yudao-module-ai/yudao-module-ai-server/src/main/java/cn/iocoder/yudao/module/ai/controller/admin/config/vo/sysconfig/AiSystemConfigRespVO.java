package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 系统配置 Response VO")
@Data
public class AiSystemConfigRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "配置键", example = "ai.default.model")
    private String configKey;

    @Schema(description = "配置值", example = "gpt-3.5-turbo")
    private String configValue;

    @Schema(description = "值类型", example = "string")
    private String valueType;

    @Schema(description = "配置描述", example = "默认模型配置")
    private String description;

    @Schema(description = "配置分类", example = "model")
    private String category;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
