package cn.iocoder.yudao.module.ai.controller.app.social.vo.leaderboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "用户 App - 排行榜用户 Response VO")
@Data
public class AppAiLeaderboardUserRespVO {

    @Schema(description = "排名", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer rank;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "积分", requiredMode = Schema.RequiredMode.REQUIRED, example = "500")
    private Integer score;

    @Schema(description = "等级", requiredMode = Schema.RequiredMode.REQUIRED, example = "4")
    private Integer level;

    @Schema(description = "等级称号", requiredMode = Schema.RequiredMode.REQUIRED, example = "知识达人")
    private String rankTitle;

}
