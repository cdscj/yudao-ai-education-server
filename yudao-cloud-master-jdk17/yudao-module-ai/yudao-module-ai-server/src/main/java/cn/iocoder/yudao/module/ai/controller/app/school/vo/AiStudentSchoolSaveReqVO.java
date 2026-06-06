package cn.iocoder.yudao.module.ai.controller.app.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "用户 App - 学生学校绑定/更新 Request VO")
@Data
public class AiStudentSchoolSaveReqVO {

    @Schema(description = "学校编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "学校编号不能为空")
    private Long schoolId;

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

}
