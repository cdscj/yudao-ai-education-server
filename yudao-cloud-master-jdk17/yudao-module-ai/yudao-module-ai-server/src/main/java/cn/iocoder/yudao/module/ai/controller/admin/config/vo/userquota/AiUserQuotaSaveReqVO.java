package cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI 用户配额新增/修改 Request VO")
@Data
public class AiUserQuotaSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    @Schema(description = "配额类型", example = "1")
    private Integer quotaType;

    @Schema(description = "每日限额", example = "100")
    private Long dailyLimit;

    @Schema(description = "每月限额", example = "3000")
    private Long monthlyLimit;

    @Schema(description = "总限额", example = "100000")
    private Long totalLimit;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
