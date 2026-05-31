package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 学习资源分页 Request VO")
@Data
public class LearningResourcePageReqVO extends PageParam {
    @Schema(description = "用户编号")
    private Long userId;
    @Schema(description = "资源类型")
    private String resourceType;
    @Schema(description = "标题")
    private String title;
}
