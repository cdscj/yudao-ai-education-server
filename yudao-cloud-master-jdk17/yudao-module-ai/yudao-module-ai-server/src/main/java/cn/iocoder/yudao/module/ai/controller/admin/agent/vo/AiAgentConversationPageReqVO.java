package cn.iocoder.yudao.module.ai.controller.admin.agent.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI Agent 会话分页 Request VO")
@Data
public class AiAgentConversationPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "Agent 配置编号", example = "1")
    private Long agentId;

    @Schema(description = "会话标题", example = "Java 问题")
    private String title;

    @Schema(description = "会话状态", example = "true")
    private Boolean active;

}
