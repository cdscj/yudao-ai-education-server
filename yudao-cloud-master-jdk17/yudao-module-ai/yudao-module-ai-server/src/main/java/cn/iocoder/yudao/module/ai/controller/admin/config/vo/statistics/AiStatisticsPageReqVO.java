package cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "管理后台 - AI 统计分页 Request VO")
@Data
public class AiStatisticsPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "模型编号", example = "1")
    private Long modelId;

    @Schema(description = "API 类型", example = "chat")
    private String apiType;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "统计日期开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate statDateStart;

    @Schema(description = "统计日期结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate statDateEnd;

}
