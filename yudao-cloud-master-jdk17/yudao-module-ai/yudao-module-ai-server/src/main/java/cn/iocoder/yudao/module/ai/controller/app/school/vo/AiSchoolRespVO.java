package cn.iocoder.yudao.module.ai.controller.app.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 学校信息 Response VO")
@Data
public class AiSchoolRespVO {

    @Schema(description = "学校编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "学校名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "清华大学")
    private String name;

    @Schema(description = "省份", example = "北京")
    private String province;

    @Schema(description = "城市", example = "北京")
    private String city;

    @Schema(description = "学校类型", example = "UNIVERSITY")
    private String type;

}
