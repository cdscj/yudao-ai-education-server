package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 作业提交 Response VO")
@Data
public class AiHomeworkSubmissionRespVO {
    private Long id;
    private Long homeworkId;
    private Long userId;
    private String answers;
    private LocalDateTime submitTime;
    private Integer totalScore;
    private Integer aiScore;
    private String aiFeedback;
    private String gradeStatus;
    private String gradeDetail;
    private Integer redoCount;
    private Integer durationSeconds;
    private LocalDateTime createTime;
}
