package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "ai_learning_path")
@KeySequence("ai_learning_path_seq")
@Data
public class AiLearningPathDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long profileId;
    private String title;
    private String goal;
    private String description;
    private Integer totalNodes;
    private Integer completedNodes;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime expectedEndTime;
    private LocalDateTime completedTime;

}
