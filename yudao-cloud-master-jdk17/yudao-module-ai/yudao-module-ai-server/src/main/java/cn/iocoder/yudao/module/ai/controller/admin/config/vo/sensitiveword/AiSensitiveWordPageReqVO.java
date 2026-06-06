package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 敏感词分页 Request VO")
@Data
public class AiSensitiveWordPageReqVO extends PageParam {

    @Schema(description = "敏感词", example = "敏感词")
    private String word;

    @Schema(description = "敏感等级", example = "1")
    private Integer level;

    @Schema(description = "分类", example = "政治")
    private String category;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
