package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - AI 系统配置新增/修改 Request VO")
@Data
public class AiSystemConfigSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED, example = "ai.default.model")
    @NotEmpty(message = "配置键不能为空")
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED, example = "gpt-3.5-turbo")
    @NotEmpty(message = "配置值不能为空")
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

}
