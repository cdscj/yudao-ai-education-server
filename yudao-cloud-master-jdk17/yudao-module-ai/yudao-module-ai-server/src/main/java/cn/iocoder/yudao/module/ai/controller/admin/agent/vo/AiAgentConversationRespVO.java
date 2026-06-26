package cn.iocoder.yudao.module.ai.controller.admin.agent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI Agent 会话 Response VO")
@Data
public class AiAgentConversationRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "Agent 配置编号", example = "1")
    private Long agentId;

    @Schema(description = "会话标题", example = "Java 多线程问题咨询")
    private String title;

    @Schema(description = "使用的 AI 模型编号", example = "1")
    private Long modelId;

    @Schema(description = "会话状态（true-进行中，false-已结束）", example = "true")
    private Boolean active;

    @Schema(description = "消息数量", example = "10")
    private Integer messageCount;

    @Schema(description = "Token 使用总数", example = "4096")
    private Integer totalTokens;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
