package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 辅导消息 Response VO")
@Data
public class TutoringMessageRespVO {
    private Long id;
    private Long sessionId;
    private String role;
    private String contentType;
    private String content;
    private String contentJson;
    private LocalDateTime createTime;
}
