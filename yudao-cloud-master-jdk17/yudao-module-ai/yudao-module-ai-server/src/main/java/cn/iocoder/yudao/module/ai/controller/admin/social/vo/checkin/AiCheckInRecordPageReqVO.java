package cn.iocoder.yudao.module.ai.controller.admin.social.vo.checkin;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Schema(description = "管理后台 - AI 签到记录分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiCheckInRecordPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "开始签到日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDateStart;

    @Schema(description = "结束签到日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkInDateEnd;

}
