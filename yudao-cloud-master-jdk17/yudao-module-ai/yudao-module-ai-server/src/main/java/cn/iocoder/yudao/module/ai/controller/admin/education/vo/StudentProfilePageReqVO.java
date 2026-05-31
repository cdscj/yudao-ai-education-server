package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 学生画像分页 Request VO")
@Data
public class StudentProfilePageReqVO extends PageParam {
    @Schema(description = "用户编号")
    private Long userId;
    @Schema(description = "专业")
    private String major;
    @Schema(description = "年级")
    private String grade;
}
