package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 知识点标签 Response VO")
@Data
public class AiKnowledgeTagRespVO {
    private Long id;
    private Long subjectId;
    private String name;
    private String description;
    private Integer difficulty;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
