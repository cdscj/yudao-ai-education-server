package cn.iocoder.yudao.module.ai.controller.admin.social.vo.points;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI 用户积分新增/调整 Request VO")
@Data
public class AiUserPointsAddReqVO {

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "积分值（正数增加，负数减少）", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @NotNull(message = "积分值不能为空")
    private Integer points;

    @Schema(description = "备注", example = "管理员手动调整")
    private String remark;

}
