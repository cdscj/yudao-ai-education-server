package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName(value = "ai_learning_path_node")
@KeySequence("ai_learning_path_node_seq")
@Data
public class AiLearningPathNodeDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long pathId;
    private Long userId;
    private String title;
    private String description;
    private String content;
    private Integer sortOrder;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime completedTime;

}
