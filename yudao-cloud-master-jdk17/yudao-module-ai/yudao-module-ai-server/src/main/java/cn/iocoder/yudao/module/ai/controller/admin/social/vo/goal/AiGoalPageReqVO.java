package cn.iocoder.yudao.module.ai.controller.admin.social.vo.goal;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 学习目标分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiGoalPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "目标类型", example = "DAILY")
    private String goalType;

    @Schema(description = "状态", example = "ACTIVE")
    private String status;

}
