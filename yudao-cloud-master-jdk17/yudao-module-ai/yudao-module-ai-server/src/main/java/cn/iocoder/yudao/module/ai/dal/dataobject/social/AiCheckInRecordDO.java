package cn.iocoder.yudao.module.ai.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * AI 签到记录 DO
 *
 * @author 芋道源码
 */
@TableName(value = "ai_check_in_record")
@KeySequence("ai_check_in_record_seq")
@Data
public class AiCheckInRecordDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 签到日期
     */
    private LocalDate checkInDate;
    /**
     * 连续签到天数
     */
    private Integer streakDays;
    /**
     * 累计签到天数
     */
    private Integer totalDays;
    /**
     * 获得积分
     */
    private Integer pointsEarned;
    /**
     * AI 鼓励语
     */
    private String aiEncouragement;

}
