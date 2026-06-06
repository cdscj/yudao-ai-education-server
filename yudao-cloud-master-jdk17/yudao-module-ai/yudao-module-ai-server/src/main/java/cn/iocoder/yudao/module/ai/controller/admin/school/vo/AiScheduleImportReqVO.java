package cn.iocoder.yudao.module.ai.controller.admin.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - 课程表批量导入 Request VO")
@Data
public class AiScheduleImportReqVO {

    @Schema(description = "学校编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "学校编号不能为空")
    private Long schoolId;

    @Schema(description = "学期", requiredMode = Schema.RequiredMode.REQUIRED, example = "2024-2025-2")
    @NotEmpty(message = "学期不能为空")
    private String semester;

    @Schema(description = "课程列表", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "课程列表不能为空")
    @Valid
    private List<AiScheduleImportItemVO> courses;

}
