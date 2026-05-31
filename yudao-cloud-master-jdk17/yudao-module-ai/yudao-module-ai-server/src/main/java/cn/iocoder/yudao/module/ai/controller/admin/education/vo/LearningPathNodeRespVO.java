package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学习路径节点 Response VO")
@Data
public class LearningPathNodeRespVO {
    private Long id;
    private Long pathId;
    private String title;
    private String description;
    private String content;
    private Integer sortOrder;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime completedTime;
    private LocalDateTime createTime;
}
