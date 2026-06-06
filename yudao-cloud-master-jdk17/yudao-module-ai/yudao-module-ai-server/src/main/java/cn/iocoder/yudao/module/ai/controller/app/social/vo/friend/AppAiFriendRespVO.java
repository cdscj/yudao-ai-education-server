package cn.iocoder.yudao.module.ai.controller.app.social.vo.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 好友 Response VO")
@Data
public class AppAiFriendRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "好友用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    private Long friendUserId;

    @Schema(description = "好友昵称", example = "李四")
    private String nickname;

    @Schema(description = "好友头像", example = "http://example.com/avatar.png")
    private String avatar;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    @Schema(description = "备注", example = "一起学习的好友")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
