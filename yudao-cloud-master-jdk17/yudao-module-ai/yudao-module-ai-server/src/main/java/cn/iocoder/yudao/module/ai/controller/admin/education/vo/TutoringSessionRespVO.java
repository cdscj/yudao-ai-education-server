package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - 辅导会话 Response VO")
@Data
public class TutoringSessionRespVO {
    private Long id;
    private Long userId;
    private String title;
    private String question;
    private String status;
    private LocalDateTime createTime;
}
