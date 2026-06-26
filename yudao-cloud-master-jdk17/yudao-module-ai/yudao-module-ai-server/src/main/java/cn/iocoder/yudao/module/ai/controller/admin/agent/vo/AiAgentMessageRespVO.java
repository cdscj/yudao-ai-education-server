package cn.iocoder.yudao.module.ai.controller.admin.agent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI Agent 消息 Response VO")
@Data
public class AiAgentMessageRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "会话编号", example = "1")
    private Long conversationId;

    @Schema(description = "消息角色（user/assistant）", example = "assistant")
    private String role;

    @Schema(description = "消息内容", example = "多线程问题的解决方案是...")
    private String content;

    @Schema(description = "是否为错误消息", example = "false")
    private Boolean isError;

    @Schema(description = "消耗的 Token 数", example = "512")
    private Integer usageTokens;

    @Schema(description = "推理过程（JSON）", example = "[{\"step\":1,\"thought\":\"...\"}]")
    private String reasoning;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
