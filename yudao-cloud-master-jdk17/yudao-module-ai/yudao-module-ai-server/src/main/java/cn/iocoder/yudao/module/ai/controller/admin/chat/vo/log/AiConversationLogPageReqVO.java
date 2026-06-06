package cn.iocoder.yudao.module.ai.controller.admin.chat.vo.log;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Schema(description = "管理后台 - AI 对话日志分页 Request VO")
@Data
public class AiConversationLogPageReqVO extends PageParam {

    @Schema(description = "用户编号", example = "1")
    private Long userId;

    @Schema(description = "对话编号", example = "1")
    private Long conversationId;

    @Schema(description = "模型名称", example = "GPT-3.5")
    private String modelName;

    @Schema(description = "是否成功", example = "true")
    private Boolean success;

    @Schema(description = "创建时间开始")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;

}
