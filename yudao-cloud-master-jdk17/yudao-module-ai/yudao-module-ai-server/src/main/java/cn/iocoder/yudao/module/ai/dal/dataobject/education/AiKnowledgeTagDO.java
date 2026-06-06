package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ai_knowledge_tag")
@KeySequence("ai_knowledge_tag_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiKnowledgeTagDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long subjectId;
    private String name;
    private String description;
    private Integer difficulty;
    private Integer sort;
    private Integer status;
}
