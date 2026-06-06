package cn.iocoder.yudao.module.ai.controller.admin.social.vo.leaderboard;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "管理后台 - AI 排行榜分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiLeaderboardPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "周期类型（DAILY / WEEKLY / MONTHLY）", example = "WEEKLY")
    private String periodType;

    @Schema(description = "快照日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate snapshotDate;

}
