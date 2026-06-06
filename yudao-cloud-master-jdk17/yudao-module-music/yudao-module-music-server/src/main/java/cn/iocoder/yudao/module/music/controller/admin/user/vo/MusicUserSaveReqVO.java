package cn.iocoder.yudao.module.music.controller.admin.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
@Schema(description = "管理后台 - 音乐用户新增/修改 Request VO")
public class MusicUserSaveReqVO {

    @Schema(description = "用户编号", example = "1")
    private Long id;

    @NotBlank(message = "用户账号不能为空")
    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "手机号")
    private String mobile;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态")
    private Integer status;

}