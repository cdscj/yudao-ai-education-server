package cn.iocoder.yudao.module.ai.controller.app.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 学生学校信息 Response VO")
@Data
public class AiStudentSchoolRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "学校编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long schoolId;

    @Schema(description = "学校名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "清华大学")
    private String schoolName;

    @Schema(description = "专业", example = "计算机科学与技术")
    private String major;

    @Schema(description = "年级", example = "2024级")
    private String grade;

    @Schema(description = "班级", example = "计科2401班")
    private String className;

    @Schema(description = "学号", example = "2024001001")
    private String studentNo;

    @Schema(description = "入学年份", example = "2024")
    private Integer enrollmentYear;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
