package cn.iocoder.yudao.module.ai.controller.app.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 学习资源 Response VO")
@Data
public class AppLearningResourceRespVO {
    private Long id;
    private String title;
    private String resourceType;
    private String content;
    private String difficulty;
    private String status;
    private LocalDateTime createTime;
}
