package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 题库新增/修改 Request VO")
@Data
public class AiQuestionBankSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "学科编号", example = "1")
    private Long subjectId;

    @Schema(description = "知识点标签编号JSON", example = "[1,2,3]")
    private String knowledgeTagIds;

    @Schema(description = "题目类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "CHOICE")
    @NotEmpty(message = "题目类型不能为空")
    private String questionType;

    @Schema(description = "难度", example = "3")
    @NotNull(message = "难度不能为空")
    private Integer difficulty;

    @Schema(description = "题干", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "题干不能为空")
    private String title;

    @Schema(description = "题目内容")
    private String content;

    @Schema(description = "选项JSON")
    private String options;

    @Schema(description = "正确答案")
    private String answer;

    @Schema(description = "答案解析")
    private String analysis;

    @Schema(description = "编程题配置JSON")
    private String programmingConfig;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "排序", example = "0")
    private Integer sort;
}
