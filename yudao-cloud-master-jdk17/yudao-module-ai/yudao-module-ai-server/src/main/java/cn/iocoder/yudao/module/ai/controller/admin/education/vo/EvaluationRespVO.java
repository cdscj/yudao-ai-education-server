package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学习评估 Response VO")
@Data
public class EvaluationRespVO {
    private Long id;
    private Long userId;
    private Long profileId;
    private String evaluationType;
    private String dimension;
    private Integer score;
    private Integer maxScore;
    private String evaluation;
    private String suggestion;
    private String evaluationJson;
    private LocalDateTime createTime;
}
