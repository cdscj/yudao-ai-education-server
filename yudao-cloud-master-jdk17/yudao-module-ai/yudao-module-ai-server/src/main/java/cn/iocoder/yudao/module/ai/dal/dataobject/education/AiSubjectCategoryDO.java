package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ai_subject_category")
@KeySequence("ai_subject_category_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSubjectCategoryDO extends TenantBaseDO {

    @TableId
    private Long id;
    private String name;
    private String code;
    private Long parentId;
    private String icon;
    private String description;
    private Integer sort;
    private Integer status;
}
