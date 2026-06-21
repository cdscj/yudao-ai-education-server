package cn.iocoder.yudao.module.ai.dal.dataobject.config;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.*;

/**
 * AI 提示词模板 DO
 *
 * @author 芋道源码
 */
@TableName(value = "ai_prompt_template", autoResultMap = true)
@KeySequence("ai_prompt_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiPromptTemplateDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 模板名称
     */
    private String name;
    /**
     * 模板分类
     */
    private String category;
    /**
     * 模板描述
     */
    private String description;
    /**
     * 模板内容
     */
    private String content;
    /**
     * 变量列表（JSON）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String variables;
    /**
     * 模板类型
     *
     * 枚举 {@link CommonStatusEnum}
     */
    private Integer type;
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

    /**
     * 模板唯一标识码（用于A/B测试查找）
     */
    private String code;

    /**
     * 版本号
     */
    private Integer version;

    /**
     * A/B 测试分组
     *
     * A 组 / B 组 / NULL（不参与 A/B 测试）
     */
    private String abGroup;

    /**
     * 上一版本 ID（版本链）
     */
    private Long parentId;

}
