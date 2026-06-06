package cn.iocoder.yudao.module.ai.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AI 用户动态 DO
 *
 * @author 芋道源码
 */
@TableName(value = "ai_user_activity")
@KeySequence("ai_user_activity_seq")
@Data
public class AiUserActivityDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 动态类型
     */
    private Integer activityType;
    /**
     * 动态内容
     */
    private String content;
    /**
     * 关联编号
     */
    private Long refId;

}
