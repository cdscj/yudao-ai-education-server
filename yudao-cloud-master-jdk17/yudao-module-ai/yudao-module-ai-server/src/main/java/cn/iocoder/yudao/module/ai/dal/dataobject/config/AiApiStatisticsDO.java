package cn.iocoder.yudao.module.ai.dal.dataobject.config;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

/**
 * AI API 统计 DO
 *
 * 记录每个用户、每个模型 API 的调用统计信息，跨租户统计
 *
 * @author 芋道源码
 */
@TableName("ai_api_statistics")
@KeySequence("ai_api_statistics_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class AiApiStatisticsDO {

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
     * 模型编号
     */
    private Long modelId;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 平台
     */
    private String platform;
    /**
     * API 类型
     */
    private String apiType;
    /**
     * 调用次数
     */
    private Integer callCount;
    /**
     * 输入 Token 数
     */
    private Integer inputTokens;
    /**
     * 输出 Token 数
     */
    private Integer outputTokens;
    /**
     * 总 Token 数
     */
    private Integer totalTokens;
    /**
     * 耗时（毫秒）
     */
    private Long durationMs;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 统计日期
     */
    private LocalDate statDate;

}
