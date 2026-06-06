package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学习计划 Response VO")
@Data
public class AiStudyPlanRespVO {
    private Long id;
    private Long userId;
    private String title;
    private String planType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String goal;
    private String description;
    private String aiGeneratedContent;
    private String dailyPlans;
    private String status;
    private Integer progress;
    private String source;
    private LocalDateTime completedDate;
    private LocalDateTime createTime;
}
