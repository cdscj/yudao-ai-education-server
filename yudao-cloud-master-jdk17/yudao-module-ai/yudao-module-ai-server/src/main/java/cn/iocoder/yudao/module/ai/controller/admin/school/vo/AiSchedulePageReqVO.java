package cn.iocoder.yudao.module.ai.controller.admin.school.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 课程表分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiSchedulePageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "课程名称", example = "高等数学")
    private String courseName;

    @Schema(description = "星期", example = "1")
    private Integer dayOfWeek;

    @Schema(description = "学期", example = "2024-2025-1")
    private String semester;

}
