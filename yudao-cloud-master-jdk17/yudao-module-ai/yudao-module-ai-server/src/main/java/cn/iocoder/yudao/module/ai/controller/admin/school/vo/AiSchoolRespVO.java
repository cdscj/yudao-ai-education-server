package cn.iocoder.yudao.module.ai.controller.admin.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 学校信息响应 VO")
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

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
