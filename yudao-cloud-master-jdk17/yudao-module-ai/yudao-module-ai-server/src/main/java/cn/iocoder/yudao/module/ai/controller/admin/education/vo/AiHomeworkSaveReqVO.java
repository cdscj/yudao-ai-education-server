package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 作业新增/修改 Request VO")
@Data
public class AiHomeworkSaveReqVO {
    private Long id;
    @NotEmpty(message = "作业标题不能为空")
    private String title;
    private String description;
    private Long subjectId;
    @NotEmpty(message = "题目列表不能为空")
    private String questionIds;
    private Integer totalScore;
    private Integer passScore;
    private LocalDateTime deadline;
    private Integer timeLimit;
    private Boolean allowRedo;
    private Integer maxRedoCount;
}
