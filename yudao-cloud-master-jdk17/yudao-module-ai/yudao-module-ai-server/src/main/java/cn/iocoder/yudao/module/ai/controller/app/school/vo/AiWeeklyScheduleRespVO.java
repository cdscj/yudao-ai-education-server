package cn.iocoder.yudao.module.ai.controller.app.school.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Schema(description = "用户 App - 周课程表 Response VO")
@Data
public class AiWeeklyScheduleRespVO {

    @Schema(description = "周课程表（key=星期1-7，value=课程列表）")
    private Map<Integer, List<AiCourseRespVO>> weeklySchedule;

}
