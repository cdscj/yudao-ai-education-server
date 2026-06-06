package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@TableName("ai_study_plan")
@KeySequence("ai_study_plan_seq")
@Data @EqualsAndHashCode(callSuper = true) @ToString(callSuper = true)
@Builder @NoArgsConstructor @AllArgsConstructor
public class AiStudyPlanDO extends TenantBaseDO {
    @TableId private Long id;
    private Long userId;
    private String title;
    private String planType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String goal;
    private String description;
    private String aiGeneratedContent;
    private String dailyPlans;
    private String status;
    private Integer progress;
    private String source;
    private LocalDateTime completedDate;
}
