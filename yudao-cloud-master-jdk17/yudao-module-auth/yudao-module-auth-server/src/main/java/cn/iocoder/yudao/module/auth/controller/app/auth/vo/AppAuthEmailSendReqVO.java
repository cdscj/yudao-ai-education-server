package cn.iocoder.yudao.module.auth.controller.app.auth.vo;

import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "APP - 发送邮箱验证码 Request VO")
@Data
public class AppAuthEmailSendReqVO {

    @Schema(description = "邮箱", example = "yudao@iocoder.cn")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "发送场景", example = "2")
    @NotNull(message = "发送场景不能为空")
    @InEnum(SmsSceneEnum.class)
    private Integer scene;

}