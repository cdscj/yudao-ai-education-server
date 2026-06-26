package cn.iocoder.yudao.module.ai.controller.admin.agent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI Agent 对话发送 Request VO")
@Data
public class AiAgentChatSendReqVO {

    @Schema(description = "Agent 配置编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "Agent 配置编号不能为空")
    private Long agentId;

    @Schema(description = "会话编号（首次对话可为空，系统自动创建）", example = "1")
    private Long conversationId;

    @Schema(description = "消息内容", requiredMode = Schema.RequiredMode.REQUIRED, example = "帮我写一个排序算法")
    @NotEmpty(message = "消息内容不能为空")
    private String content;

}
