package cn.iocoder.yudao.module.ai.dal.dataobject.school;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI 学校信息 DO
 *
 * @author 芋道源码
 */
@TableName("ai_school")
@KeySequence("ai_school_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSchoolDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 学校名称
     */
    private String name;
    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 学校类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.ai.enums.school.AiSchoolTypeEnum}
     */
    private String type;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;

}
