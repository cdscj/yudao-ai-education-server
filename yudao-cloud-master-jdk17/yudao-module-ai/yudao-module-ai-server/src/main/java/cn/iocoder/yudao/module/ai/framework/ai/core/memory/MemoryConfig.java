package cn.iocoder.yudao.module.ai.framework.ai.core.memory;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 对话记忆配置
 *
 * @author yudao
 */
@ConfigurationProperties(prefix = "yudao.ai.memory")
@Data
public class MemoryConfig {

    /** 触发摘要的 token 阈值 */
    private int summaryThreshold = 4000;

    /** 保留的最近消息轮数 */
    private int keepRecentRounds = 5;

    /** 是否启用长期向量记忆 */
    private boolean vectorMemoryEnabled = false;
}
