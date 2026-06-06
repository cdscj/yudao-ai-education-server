package cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - AI 统计趋势 Request VO")
@Data
public class AiStatisticsTrendReqVO {

    @Schema(description = "开始日期", example = "2024-01-01")
    private String startDate;

    @Schema(description = "结束日期", example = "2024-01-31")
    private String endDate;

    @Schema(description = "粒度(1=小时 2=天 3=周 4=月)", example = "2")
    private Integer granularity;

}
