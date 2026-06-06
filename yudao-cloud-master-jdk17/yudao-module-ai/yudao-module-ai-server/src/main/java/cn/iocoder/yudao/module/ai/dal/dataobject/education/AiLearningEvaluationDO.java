package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName(value = "ai_learning_evaluation")
@KeySequence("ai_learning_evaluation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiLearningEvaluationDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long profileId;
    private String evaluationType;
    private String dimension;
    private Integer score;
    private Integer maxScore;
    private String evaluation;
    private String suggestion;
    private String evaluationJson;

}
