package cn.iocoder.yudao.module.auth.controller.app.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Schema(description = "APP - 修改邮箱 Request VO")
@Data
public class AppUserUpdateEmailReqVO {

    @Schema(description = "新邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "yudao@iocoder.cn")
    @NotEmpty(message = "新邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    @NotEmpty(message = "验证码不能为空")
    private String code;

}