package cn.iocoder.yudao.module.auth.controller.app.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Schema(description = "APP - 邮箱密码登录 Request VO")
@Data
public class AppAuthLoginReqVO {

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "user@example.com")
    @NotEmpty(message = "邮箱不能为空")
    private String email;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "社交类型")
    private Integer socialType;

    @Schema(description = "社交授权码")
    private String socialCode;

    @Schema(description = "社交 state")
    private String socialState;

}