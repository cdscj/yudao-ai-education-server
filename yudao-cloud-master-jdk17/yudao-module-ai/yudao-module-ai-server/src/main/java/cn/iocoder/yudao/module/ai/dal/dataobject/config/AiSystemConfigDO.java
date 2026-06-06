package cn.iocoder.yudao.module.ai.dal.dataobject.config;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI 系统配置 DO
 *
 * 全局系统配置，跨租户生效
 *
 * @author 芋道源码
 */
@TableName("ai_system_config")
@KeySequence("ai_system_config_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class AiSystemConfigDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 配置键
     */
    private String configKey;
    /**
     * 配置值
     */
    private String configValue;
    /**
     * 值类型
     */
    private String valueType;
    /**
     * 配置描述
     */
    private String description;
    /**
     * 配置分类
     */
    private String category;
    /**
     * 排序值
     */
    private Integer sort;
    /**
     * 状态
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer status;

}
