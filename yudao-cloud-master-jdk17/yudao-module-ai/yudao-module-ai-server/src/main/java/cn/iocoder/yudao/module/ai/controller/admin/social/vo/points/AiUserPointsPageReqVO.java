package cn.iocoder.yudao.module.ai.controller.admin.social.vo.points;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 用户积分分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiUserPointsPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "等级", example = "3")
    private Integer level;

}
