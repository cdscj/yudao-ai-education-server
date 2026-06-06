package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 考试记录 Response VO")
@Data
public class AiExamRecordRespVO {
    private Long id;
    private Long examId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private String answers;
    private Integer totalScore;
    private Integer aiScore;
    private String aiFeedback;
    private String gradeDetail;
    private Integer durationSeconds;
    private String status;
    private Integer retakeCount;
    private LocalDateTime createTime;
}
