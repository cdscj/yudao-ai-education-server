package cn.iocoder.yudao.module.music.api.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息 Response VO")
public class UserDTO {

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String username;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin@iocoder.cn")
    private String email;

    @Schema(description = "个人简介", example = "音乐爱好者")
    private String introduction;
}