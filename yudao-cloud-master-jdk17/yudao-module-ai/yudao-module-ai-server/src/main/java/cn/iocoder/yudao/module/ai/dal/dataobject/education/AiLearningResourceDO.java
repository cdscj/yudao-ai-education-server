package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName(value = "ai_learning_resource")
@KeySequence("ai_learning_resource_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiLearningResourceDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long profileId;
    private String title;
    private String resourceType;
    private String content;
    private String contentJson;
    private String tags;
    private String difficulty;
    private Long relatedCourseId;
    private String courseName;
    private String status;
    private String errorMessage;
    private Integer progress;
    private String feedback;

}
