package cn.iocoder.yudao.module.ai.controller.admin.social.vo.friend;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 好友分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiFriendPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "好友用户编号", example = "2048")
    private Long friendUserId;

    @Schema(description = "状态（0-待确认，1-已确认，2-已拉黑）", example = "1")
    private Integer status;

}
