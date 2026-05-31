package cn.iocoder.yudao.module.ai.controller.app.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 学习路径 Response VO")
@Data
public class AppLearningPathRespVO {
    private Long id;
    private String title;
    private String goal;
    private String description;
    private Integer totalNodes;
    private Integer completedNodes;
    private String status;
    private LocalDateTime createTime;
}
