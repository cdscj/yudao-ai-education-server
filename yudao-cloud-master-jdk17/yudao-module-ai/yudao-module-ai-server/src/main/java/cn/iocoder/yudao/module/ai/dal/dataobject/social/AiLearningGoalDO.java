package cn.iocoder.yudao.module.ai.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

/**
 * AI 学习目标 DO
 *
 * @author 芋道源码
 */
@TableName(value = "ai_learning_goal")
@KeySequence("ai_learning_goal_seq")
@Data
public class AiLearningGoalDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 目标类型
     */
    private String goalType;
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 目标值
     */
    private Integer targetValue;
    /**
     * 当前值
     */
    private Integer currentValue;
    /**
     * 截止时间
     */
    private LocalDate deadline;
    /**
     * 状态
     */
    private String status;
    /**
     * AI 反馈
     */
    private String aiFeedback;

}
