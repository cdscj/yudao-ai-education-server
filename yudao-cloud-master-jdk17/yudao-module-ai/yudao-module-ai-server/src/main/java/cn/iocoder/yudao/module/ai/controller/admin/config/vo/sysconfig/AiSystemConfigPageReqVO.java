package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 系统配置分页 Request VO")
@Data
public class AiSystemConfigPageReqVO extends PageParam {

    @Schema(description = "配置键", example = "ai.default.model")
    private String configKey;

    @Schema(description = "配置分类", example = "model")
    private String category;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
