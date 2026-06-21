package cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSensitiveWordDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiSensitiveWordMapper;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 敏感词检查器
 *
 * @author yudao
 */
@Component
@Slf4j
public class SensitiveWordChecker {

    @Resource
    private AiSensitiveWordMapper sensitiveWordMapper;

    /** 敏感词缓存（启动加载，定期刷新） */
    private volatile List<String> cachedWords = List.of();
    private volatile long lastLoadTime = 0;
    private static final long CACHE_TTL_MS = 300_000; // 5分钟刷新

    public AiSafetyResult.Level check(String content) {
        List<String> words = getSensitiveWords();
        if (CollUtil.isEmpty(words)) {
            return AiSafetyResult.Level.PASS;
        }
        String lowerContent = content.toLowerCase();
        for (String word : words) {
            if (lowerContent.contains(word.toLowerCase())) {
                log.warn("[SensitiveWord] 检测到敏感词: {}", word);
                return AiSafetyResult.Level.BLOCK;
            }
        }
        return AiSafetyResult.Level.PASS;
    }

    private List<String> getSensitiveWords() {
        if (System.currentTimeMillis() - lastLoadTime > CACHE_TTL_MS) {
            synchronized (this) {
                if (System.currentTimeMillis() - lastLoadTime > CACHE_TTL_MS) {
                    try {
                        List<AiSensitiveWordDO> all = sensitiveWordMapper.selectList();
                        cachedWords = all.stream()
                                .map(AiSensitiveWordDO::getWord)
                                .collect(Collectors.toList());
                        lastLoadTime = System.currentTimeMillis();
                    } catch (Exception e) {
                        log.warn("[SensitiveWord] 加载敏感词失败，使用缓存", e);
                    }
                }
            }
        }
        return cachedWords;
    }
}
