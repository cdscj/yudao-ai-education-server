package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.log;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 对话日志 Response VO")
@Data
public class AiConversationLogRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "对话编号", example = "1")
    private Long conversationId;

    @Schema(description = "消息编号", example = "1")
    private Long messageId;

    @Schema(description = "模型名称", example = "GPT-3.5")
    private String modelName;

    @Schema(description = "用户输入", example = "你好")
    private String prompt;

    @Schema(description = "模型输出", example = "你好，有什么可以帮助你的？")
    private String completion;

    @Schema(description = "输入 Token 数", example = "10")
    private Integer inputTokens;

    @Schema(description = "输出 Token 数", example = "50")
    private Integer outputTokens;

    @Schema(description = "耗时(毫秒)", example = "500")
    private Integer durationMs;

    @Schema(description = "IP 地址", example = "127.0.0.1")
    private String ipAddress;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "错误信息", example = "连接超时")
    private String errorMessage;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
