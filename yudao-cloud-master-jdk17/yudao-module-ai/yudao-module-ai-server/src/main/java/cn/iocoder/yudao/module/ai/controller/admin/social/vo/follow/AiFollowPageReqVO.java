package cn.iocoder.yudao.module.ai.controller.admin.social.vo.follow;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 关注分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiFollowPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "被关注用户编号", example = "2048")
    private Long followUserId;

}
