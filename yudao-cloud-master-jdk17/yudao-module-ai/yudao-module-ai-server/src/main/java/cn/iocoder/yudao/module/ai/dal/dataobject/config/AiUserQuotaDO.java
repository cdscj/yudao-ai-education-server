package cn.iocoder.yudao.module.ai.dal.dataobject.config;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * AI 用户配额 DO
 *
 * @author 芋道源码
 */
@TableName("ai_user_quota")
@KeySequence("ai_user_quota_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiUserQuotaDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 配额类型
     */
    private Integer quotaType;
    /**
     * 每日限制
     */
    private Integer dailyLimit;
    /**
     * 每月限制
     */
    private Integer monthlyLimit;
    /**
     * 总限制
     */
    private Integer totalLimit;
    /**
     * 每日已用
     */
    private Integer dailyUsed;
    /**
     * 每月已用
     */
    private Integer monthlyUsed;
    /**
     * 总计已用
     */
    private Integer totalUsed;
    /**
     * 最后重置日期
     */
    private LocalDate lastResetDate;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
