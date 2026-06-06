package cn.iocoder.yudao.module.ai.controller.admin.social.vo.leaderboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 排行榜响应 VO")
@Data
public class AiLeaderboardRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "周期类型（DAILY / WEEKLY / MONTHLY）", requiredMode = Schema.RequiredMode.REQUIRED, example = "WEEKLY")
    private String periodType;

    @Schema(description = "积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    private Integer score;

    @Schema(description = "排名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer rank;

    @Schema(description = "快照日期", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate snapshotDate;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
