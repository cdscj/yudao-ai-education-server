package cn.iocoder.yudao.module.ai.controller.admin.agent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI Agent 配置 Response VO")
@Data
public class AiAgentConfigRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "Agent 名称", example = "代码助手")
    private String name;

    @Schema(description = "Agent 类型", example = "1")
    private Integer type;

    @Schema(description = "关联的 AI 模型编号", example = "1")
    private Long modelId;

    @Schema(description = "系统提示词", example = "你是一个专业的代码助手...")
    private String systemPrompt;

    @Schema(description = "Agent 描述", example = "帮助用户编写和调试代码")
    private String description;

    @Schema(description = "最大推理步数（ReAct 模式）", example = "10")
    private Integer maxSteps;

    @Schema(description = "温度参数", example = "0.7")
    private Double temperature;

    @Schema(description = "单次回复最大 Token 数", example = "4096")
    private Integer maxTokens;

    @Schema(description = "排序值", example = "1")
    private Integer sort;

    @Schema(description = "状态", example = "0")
    private Integer status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
