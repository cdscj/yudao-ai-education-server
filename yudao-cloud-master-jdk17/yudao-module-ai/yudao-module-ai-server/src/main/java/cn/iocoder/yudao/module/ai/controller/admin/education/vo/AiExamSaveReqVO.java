package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 考试新增/修改 Request VO")
@Data
public class AiExamSaveReqVO {
    private Long id;
    @NotEmpty(message = "考试标题不能为空")
    private String title;
    private Long subjectId;
    private String description;
    @NotEmpty(message = "题目列表不能为空")
    private String questionIds;
    private Integer totalScore;
    private Integer passScore;
    private Integer timeLimit;
    private Integer difficulty;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allowRetake;
    private Integer maxRetakeCount;
}
