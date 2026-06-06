package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@TableName("ai_exam")
@KeySequence("ai_exam_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiExamDO extends TenantBaseDO {
    @TableId private Long id;
    private String title;
    private Long subjectId;
    private String description;
    private String questionIds;
    private Integer totalScore;
    private Integer passScore;
    private Integer timeLimit;
    private Integer difficulty;
    private String generateType;
    private String publishStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Boolean allowRetake;
    private Integer maxRetakeCount;
}
