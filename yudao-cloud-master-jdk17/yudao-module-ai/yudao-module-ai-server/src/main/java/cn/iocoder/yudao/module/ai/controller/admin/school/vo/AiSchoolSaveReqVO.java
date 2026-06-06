package cn.iocoder.yudao.module.ai.controller.admin.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Schema(description = "管理后台 - AI 学校保存 Request VO")
@Data
public class AiSchoolSaveReqVO {

    @Schema(description = "学校编号（更新时必填）", example = "1")
    private Long id;

    @Schema(description = "学校名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "清华大学")
    @NotEmpty(message = "学校名称不能为空")
    private String name;

    @Schema(description = "省份", example = "北京")
    private String province;

    @Schema(description = "城市", example = "北京")
    private String city;

    @Schema(description = "学校类型", example = "UNIVERSITY")
    private String type;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
