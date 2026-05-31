package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学生画像 Response VO")
@Data
public class StudentProfileRespVO {
    private Long id;
    private Long userId;
    private String major;
    private String grade;
    private String learningGoals;
    private String knowledgeLevel;
    private String learningPreferences;
    private String weakPoints;
    private String strongPoints;
    private String studyHistory;
    private Integer learningSpeed;
    private String preferredResourceTypes;
    private String studyTimePreference;
    private String status;
    private String profileJson;
    private LocalDateTime createTime;
}
