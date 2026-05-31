package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "管理后台 - 学习路径 Response VO")
@Data
public class LearningPathRespVO {
    private Long id;
    private Long userId;
    private Long profileId;
    private String title;
    private String goal;
    private String description;
    private Integer totalNodes;
    private Integer completedNodes;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime expectedEndTime;
    private LocalDateTime completedTime;
    private LocalDateTime createTime;
    private List<LearningPathNodeRespVO> nodes;
}
