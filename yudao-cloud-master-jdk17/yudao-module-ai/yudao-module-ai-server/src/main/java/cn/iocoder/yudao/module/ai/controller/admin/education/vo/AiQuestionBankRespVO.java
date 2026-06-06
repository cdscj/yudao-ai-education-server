package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 题库 Response VO")
@Data
public class AiQuestionBankRespVO {
    private Long id;
    private Long subjectId;
    private String knowledgeTagIds;
    private String questionType;
    private Integer difficulty;
    private String title;
    private String content;
    private String options;
    private String answer;
    private String analysis;
    private String programmingConfig;
    private String source;
    private Integer status;
    private Integer sort;
    private LocalDateTime createTime;
}
