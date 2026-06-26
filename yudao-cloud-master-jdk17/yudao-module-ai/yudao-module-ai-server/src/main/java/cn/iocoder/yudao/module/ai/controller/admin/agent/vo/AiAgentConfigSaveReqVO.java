package cn.iocoder.yudao.module.ai.controller.admin.agent.vo;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.validation.InEnum;
import cn.iocoder.yudao.module.ai.enums.agent.AiAgentTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - AI Agent 配置新增/修改 Request VO")
@Data
public class AiAgentConfigSaveReqVO {

    @Schema(description = "编号", example = "1")
    private Long id;

    @Schema(description = "Agent 名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "代码助手")
    @NotEmpty(message = "Agent 名称不能为空")
    private String name;

    @Schema(description = "Agent 类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "Agent 类型不能为空")
    @InEnum(AiAgentTypeEnum.class)
    private Integer type;

    @Schema(description = "关联的 AI 模型编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "AI 模型编号不能为空")
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

    @Schema(description = "排序值", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "排序值不能为空")
    private Integer sort;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @NotNull(message = "状态不能为空")
    @InEnum(CommonStatusEnum.class)
    private Integer status;

}
