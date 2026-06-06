package cn.iocoder.yudao.module.ai.controller.app.social.vo.activity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "用户 App - 动态 Response VO")
@Data
public class AppAiActivityRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long userId;

    @Schema(description = "用户昵称", example = "张三")
    private String nickname;

    @Schema(description = "动态类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer activityType;

    @Schema(description = "动态内容", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "关联编号", example = "1")
    private Long refId;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
