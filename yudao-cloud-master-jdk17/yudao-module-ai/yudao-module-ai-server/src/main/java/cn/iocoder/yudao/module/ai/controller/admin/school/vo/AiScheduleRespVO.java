package cn.iocoder.yudao.module.ai.controller.admin.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 课程表响应 VO")
@Data
public class AiScheduleRespVO {

    @Schema(description = "课程编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

    @Schema(description = "课程名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "高等数学")
    private String courseName;

    @Schema(description = "授课教师", example = "张教授")
    private String teacher;

    @Schema(description = "上课地点", example = "教学楼A-301")
    private String classroom;

    @Schema(description = "星期", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer dayOfWeek;

    @Schema(description = "开始时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "08:00")
    private String startTime;

    @Schema(description = "结束时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "09:35")
    private String endTime;

    @Schema(description = "开始节次", example = "1")
    private Integer startPeriod;

    @Schema(description = "结束节次", example = "2")
    private Integer endPeriod;

    @Schema(description = "课程颜色", example = "#409eff")
    private String color;

    @Schema(description = "课程类型", example = "REQUIRED")
    private String courseType;

    @Schema(description = "学期", example = "2024-2025-1")
    private String semester;

    @Schema(description = "周类型", example = "EVERY")
    private String weekType;

    @Schema(description = "备注", example = "需要带计算器")
    private String remark;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
