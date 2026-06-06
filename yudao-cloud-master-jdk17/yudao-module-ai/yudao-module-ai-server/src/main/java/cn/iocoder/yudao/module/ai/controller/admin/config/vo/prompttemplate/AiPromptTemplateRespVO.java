package cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 提示词模板 Response VO")
@Data
public class AiPromptTemplateRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "模板名称", example = "文案生成")
    private String name;

    @Schema(description = "模板分类", example = "writing")
    private String category;

    @Schema(description = "模板描述", example = "用于生成营销文案")
    private String description;

    @Schema(description = "模板内容", example = "请生成一段关于{product}的文案")
    private String content;

    @Schema(description = "模板变量", example = "product")
    private String variables;

    @Schema(description = "模板类型", example = "1")
    private Integer type;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
