package cn.iocoder.yudao.module.ai.framework.security.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模块安全配置属性
 */
@ConfigurationProperties(prefix = "yudao.ai.security")
@Component
@Data
public class AiSecurityProperties {

    /** 是否启用限流 */
    private Boolean rateLimitEnabled = true;

    /** 是否启用敏感词过滤 */
    private Boolean sensitiveWordEnabled = true;

    /** 是否启用内容审核 */
    private Boolean contentReviewEnabled = false;

    /** 是否启用配额管理 */
    private Boolean quotaEnabled = true;

    /** 每分钟最大调用次数 */
    private Integer maxCallsPerMinute = 20;

    /** 每小时最大调用次数 */
    private Integer maxCallsPerHour = 200;

}
