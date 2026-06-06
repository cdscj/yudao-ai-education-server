package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 考试 Response VO")
@Data
public class AiExamRespVO {
    private Long id;
    private String title;
    private Long subjectId;
    private String description;
    private String questionIds;
    private Integer totalScore;
    private Integer passScore;
    private Integer timeLimit;
    private Integer difficulty;
    private String generateType;
    private String publishStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allowRetake;
    private Integer maxRetakeCount;
    private LocalDateTime createTime;
}
