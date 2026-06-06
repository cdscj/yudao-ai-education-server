package cn.iocoder.yudao.module.ai.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AI 用户关注 DO
 *
 * @author 芋道源码
 */
@TableName(value = "ai_follow")
@KeySequence("ai_follow_seq")
@Data
public class AiFollowDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 被关注用户编号
     */
    private Long followUserId;

}
