package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学习资源 Response VO")
@Data
public class LearningResourceRespVO {
    private Long id;
    private Long userId;
    private Long profileId;
    private String title;
    private String resourceType;
    private String content;
    private String contentJson;
    private String tags;
    private String difficulty;
    private Long relatedCourseId;
    private String courseName;
    private String status;
    private String errorMessage;
    private Integer progress;
    private String feedback;
    private LocalDateTime createTime;
}
