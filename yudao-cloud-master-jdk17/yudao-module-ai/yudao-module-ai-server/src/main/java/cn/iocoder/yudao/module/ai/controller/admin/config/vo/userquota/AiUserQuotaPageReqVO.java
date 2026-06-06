package cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 用户配额分页 Request VO")
@Data
public class AiUserQuotaPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "配额类型", example = "1")
    private Integer quotaType;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
