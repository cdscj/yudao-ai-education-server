package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 学习路径分页 Request VO")
@Data
public class LearningPathPageReqVO extends PageParam {
    @Schema(description = "用户编号")
    private Long userId;
    @Schema(description = "状态")
    private String status;
}
