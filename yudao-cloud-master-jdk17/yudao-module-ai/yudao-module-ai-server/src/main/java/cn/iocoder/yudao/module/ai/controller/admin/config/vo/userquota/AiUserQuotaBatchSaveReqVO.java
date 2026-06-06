package cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - AI 用户配额批量设置 Request VO")
@Data
public class AiUserQuotaBatchSaveReqVO {

    @Schema(description = "用户编号列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> userIds;

    @Schema(description = "配额类型", example = "1")
    private Integer quotaType;

    @Schema(description = "每日限额", example = "100")
    private Long dailyLimit;

    @Schema(description = "每月限额", example = "3000")
    private Long monthlyLimit;

    @Schema(description = "总限额", example = "100000")
    private Long totalLimit;

}
