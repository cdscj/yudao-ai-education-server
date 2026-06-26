package cn.iocoder.yudao.module.ai.controller.admin.agent.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI Agent 配置分页 Request VO")
@Data
public class AiAgentConfigPageReqVO extends PageParam {

    @Schema(description = "Agent 名称", example = "代码助手")
    private String name;

    @Schema(description = "Agent 类型", example = "1")
    private Integer type;

    @Schema(description = "状态", example = "0")
    private Integer status;

}
