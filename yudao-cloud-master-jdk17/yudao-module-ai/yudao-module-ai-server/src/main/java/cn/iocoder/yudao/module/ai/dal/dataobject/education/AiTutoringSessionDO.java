package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName(value = "ai_tutoring_session")
@KeySequence("ai_tutoring_session_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiTutoringSessionDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String title;
    private String question;
    private Long subjectId;
    private String knowledgeTagIds;
    private String contextJson;
    private String status;

}
