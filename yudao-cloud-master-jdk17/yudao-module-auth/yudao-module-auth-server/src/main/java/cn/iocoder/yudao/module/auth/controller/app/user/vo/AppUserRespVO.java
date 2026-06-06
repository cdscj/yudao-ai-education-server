package cn.iocoder.yudao.module.auth.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 APP - 用户信息 Response VO")
@Data
public class AppUserRespVO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "用户头像")
    private String userAvatar;

    @Schema(description = "个人简介")
    private String introduction;

    @Schema(description = "状态 0正常 1禁用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}