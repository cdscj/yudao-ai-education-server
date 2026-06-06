package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@TableName("ai_homework_submission")
@KeySequence("ai_homework_submission_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiHomeworkSubmissionDO extends TenantBaseDO {
    @TableId private Long id;
    private Long homeworkId;
    private Long userId;
    private String answers;
    private LocalDateTime submitTime;
    private Integer totalScore;
    private Integer aiScore;
    private String aiFeedback;
    private String gradeStatus;
    private String gradeDetail;
    private Integer redoCount;
    private Integer durationSeconds;
}
