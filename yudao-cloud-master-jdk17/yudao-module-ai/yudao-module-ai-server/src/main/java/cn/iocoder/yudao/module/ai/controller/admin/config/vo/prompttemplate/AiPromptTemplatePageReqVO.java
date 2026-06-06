package cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 提示词模板分页 Request VO")
@Data
public class AiPromptTemplatePageReqVO extends PageParam {

    @Schema(description = "模板名称", example = "文案生成")
    private String name;

    @Schema(description = "模板分类", example = "writing")
    private String category;

    @Schema(description = "模板类型", example = "1")
    private Integer type;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
