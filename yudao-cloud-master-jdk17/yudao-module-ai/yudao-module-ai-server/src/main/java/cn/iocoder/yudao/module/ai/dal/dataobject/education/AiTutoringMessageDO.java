package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName(value = "ai_tutoring_message")
@KeySequence("ai_tutoring_message_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTutoringMessageDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long sessionId;
    private Long userId;
    private String role;
    private String contentType;
    private String content;
    private String contentJson;

}
