package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@TableName("ai_homework")
@KeySequence("ai_homework_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHomeworkDO extends TenantBaseDO {
    @TableId private Long id;
    private String title;
    private String description;
    private Long subjectId;
    private String questionIds;
    private Integer totalScore;
    private Integer passScore;
    private LocalDateTime deadline;
    private Integer timeLimit;
    private Boolean allowRedo;
    private Integer maxRedoCount;
    private String publishStatus;
    private LocalDateTime publishTime;
}
