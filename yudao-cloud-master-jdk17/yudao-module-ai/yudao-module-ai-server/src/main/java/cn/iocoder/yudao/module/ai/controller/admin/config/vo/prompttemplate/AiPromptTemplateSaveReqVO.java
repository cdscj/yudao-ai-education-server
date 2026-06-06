package cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - AI 提示词模板新增/修改 Request VO")
@Data
public class AiPromptTemplateSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "模板名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "文案生成")
    @NotEmpty(message = "模板名称不能为空")
    private String name;

    @Schema(description = "模板分类", example = "writing")
    private String category;

    @Schema(description = "模板描述", example = "用于生成营销文案")
    private String description;

    @Schema(description = "模板内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "请生成一段关于{product}的文案")
    @NotEmpty(message = "模板内容不能为空")
    private String content;

    @Schema(description = "模板变量", example = "product")
    private String variables;

    @Schema(description = "模板类型", example = "1")
    private Integer type;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
