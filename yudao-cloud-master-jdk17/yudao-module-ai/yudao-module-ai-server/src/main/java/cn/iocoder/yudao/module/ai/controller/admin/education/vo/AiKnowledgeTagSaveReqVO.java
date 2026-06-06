package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 知识点标签新增/修改 Request VO")
@Data
public class AiKnowledgeTagSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "所属学科编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "所属学科不能为空")
    private Long subjectId;

    @Schema(description = "标签名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "导数与微分")
    @NotEmpty(message = "标签名称不能为空")
    private String name;

    @Schema(description = "描述", example = "包括函数求导、微分应用等")
    private String description;

    @Schema(description = "难度", example = "3")
    private Integer difficulty;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "0")
    private Integer status;
}
