package cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI API 调用详情分页 Request VO")
@Data
public class AiApiCallDetailPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "模型编号", example = "1")
    private Long modelId;

    @Schema(description = "平台", example = "OPENAI")
    private String platform;

    @Schema(description = "API 类型", example = "chat")
    private String apiType;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "创建时间")
    private LocalDateTime[] createTime;

}
