package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 错题本 Response VO")
@Data
public class AiWrongAnswerBookRespVO {
    private Long id;
    private Long userId;
    private Long questionId;
    private Long subjectId;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private String errorType;
    private String errorAnalysis;
    private Integer reviewCount;
    private LocalDateTime lastReviewTime;
    private LocalDateTime nextReviewTime;
    private Integer masteryLevel;
    private String sourceType;
    private Long sourceId;
    private LocalDateTime createTime;
}
