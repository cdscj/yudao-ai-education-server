package cn.iocoder.yudao.module.ai.controller.app.social.vo.friend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 好友申请 Request VO")
@Data
public class AppAiFriendApplyReqVO {

    @Schema(description = "好友用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "好友用户编号不能为空")
    private Long friendUserId;

    @Schema(description = "备注", example = "一起学习吧")
    private String remark;

}
