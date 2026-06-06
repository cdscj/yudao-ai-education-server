package cn.iocoder.yudao.module.ai.controller.admin.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 课程表导入条目 VO")
@Data
public class AiScheduleImportItemVO {

    @Schema(description = "课程名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "高等数学")
    @NotEmpty(message = "课程名称不能为空")
    private String courseName;

    @Schema(description = "授课教师", example = "张教授")
    private String teacher;

    @Schema(description = "上课地点", example = "教学楼A-301")
    private String classroom;

    @Schema(description = "星期（1=周一...7=周日）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "星期不能为空")
    private Integer dayOfWeek;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "08:00")
    @NotEmpty(message = "开始时间不能为空")
    private String startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "09:35")
    @NotEmpty(message = "结束时间不能为空")
    private String endTime;

    @Schema(description = "开始节次", example = "1")
    private Integer startPeriod;

    @Schema(description = "结束节次", example = "2")
    private Integer endPeriod;

    @Schema(description = "颜色", example = "#409eff")
    private String color;

    @Schema(description = "课程类型", example = "必修")
    private String courseType;

    @Schema(description = "周类型", example = "每周")
    private String weekType;

    @Schema(description = "备注", example = "需要带计算器")
    private String remark;

}
