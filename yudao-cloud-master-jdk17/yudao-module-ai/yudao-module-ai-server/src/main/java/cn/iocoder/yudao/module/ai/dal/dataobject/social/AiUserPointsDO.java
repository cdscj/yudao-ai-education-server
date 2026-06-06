package cn.iocoder.yudao.module.ai.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AI 用户积分 DO
 *
 * @author 芋道源码
 */
@TableName(value = "ai_user_points")
@KeySequence("ai_user_points_seq")
@Data
public class AiUserPointsDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 总积分
     */
    private Integer totalPoints;
    /**
     * 本周积分
     */
    private Integer weeklyPoints;
    /**
     * 本月积分
     */
    private Integer monthlyPoints;
    /**
     * 等级
     */
    private Integer level;
    /**
     * 等级称号
     */
    private String rankTitle;

}
