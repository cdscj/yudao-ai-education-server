package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@TableName("ai_exam_record")
@KeySequence("ai_exam_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiExamRecordDO extends TenantBaseDO {
    @TableId private Long id;
    private Long examId;
    private Long userId;
    private LocalDateTime startTime;
    private LocalDateTime submitTime;
    private String answers;
    private Integer totalScore;
    private Integer aiScore;
    private String aiFeedback;
    private String gradeDetail;
    private Integer durationSeconds;
    private String status;
    private Integer retakeCount;
}
