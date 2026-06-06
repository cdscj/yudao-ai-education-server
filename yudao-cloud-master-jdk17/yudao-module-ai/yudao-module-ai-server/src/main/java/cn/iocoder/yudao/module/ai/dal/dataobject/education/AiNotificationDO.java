package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@TableName("ai_notification")
@KeySequence("ai_notification_seq")
@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
@Builder @NoArgsConstructor @AllArgsConstructor
public class AiNotificationDO extends TenantBaseDO {
    @TableId private Long id;
    private Long userId;
    private String notificationType;
    private String title;
    private String content;
    private Long refId;
    private String refType;
    private Boolean isRead;
    private LocalDateTime readTime;
}
