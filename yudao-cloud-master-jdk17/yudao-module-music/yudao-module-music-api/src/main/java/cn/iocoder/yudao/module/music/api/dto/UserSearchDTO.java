package cn.iocoder.yudao.module.music.api.dto;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserSearchDTO extends PageParam {

    @Schema(description = "用户账号")
    private String username;

    @Schema(description = "手机号码")
    private String mobile;

    @Schema(description = "状态")
    private Integer status;

}