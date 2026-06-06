package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - 学科分类新增/修改 Request VO")
@Data
public class AiSubjectCategorySaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "学科名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "高等数学")
    @NotEmpty(message = "学科名称不能为空")
    private String name;

    @Schema(description = "学科编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "MATH")
    @NotEmpty(message = "学科编码不能为空")
    private String code;

    @Schema(description = "父学科编号", example = "0")
    private Long parentId;

    @Schema(description = "图标", example = "📐")
    private String icon;

    @Schema(description = "描述", example = "包含微积分、线性代数等")
    private String description;

    @Schema(description = "排序", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "0")
    private Integer status;
}
