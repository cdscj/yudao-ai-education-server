package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 作业 Response VO")
@Data
public class AiHomeworkRespVO {
    private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String questionIds;
    private Integer totalScore;
    private Integer passScore;
    private LocalDateTime deadline;
    private Integer timeLimit;
    private Boolean allowRedo;
    private String publishStatus;
    private LocalDateTime publishTime;
    private LocalDateTime createTime;
}
