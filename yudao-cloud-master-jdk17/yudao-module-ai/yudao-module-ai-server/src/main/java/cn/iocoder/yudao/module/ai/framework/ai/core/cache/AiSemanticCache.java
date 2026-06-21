package cn.iocoder.yudao.module.ai.framework.ai.core.cache;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * AI 语义缓存 — 基于向量相似度 + 精确匹配的两级缓存
 *
 * <p>L1 缓存 (Caffeine 内存):
 * <ul>
 *   <li>精确匹配: key = md5(systemPrompt + modelName + userInput)</li>
 *   <li>TTL: 1 小时</li>
 *   <li>最大条目: 10,000</li>
 * </ul>
 * </p>
 *
 * <p>L2 缓存 (Redis 向量存储，可选):
 * <ul>
 *   <li>相似度匹配: embedding 余弦相似度 > 0.92</li>
 *   <li>TTL: 24 小时</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AiSemanticCache {

    /**
     * L1 精确匹配缓存
     */
    private final Cache<String, CacheEntry> exactCache = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofHours(1))
            .recordStats()
            .build();

    /**
     * 缓存命中率
     */
    public double getHitRate() {
        return exactCache.stats().hitRate();
    }

    /**
     * 检查精确匹配缓存
     *
     * @param systemPrompt 系统提示词
     * @param userInput    用户输入
     * @param modelName    模型名称
     * @return 缓存的响应，未命中则返回 null
     */
    public String getExact(String systemPrompt, String userInput, String modelName) {
        String key = buildExactKey(systemPrompt, userInput, modelName);
        CacheEntry entry = exactCache.getIfPresent(key);
        if (entry != null) {
            log.debug("[SemanticCache] 精确匹配命中: key={}", key.substring(0, 16));
            return entry.getResponse();
        }
        return null;
    }

    /**
     * 写入精确匹配缓存
     */
    public void putExact(String systemPrompt, String userInput, String modelName, String response) {
        if (StrUtil.isBlank(response) || response.length() < 10) {
            return; // 太短的响应不值得缓存
        }
        String key = buildExactKey(systemPrompt, userInput, modelName);
        exactCache.put(key, new CacheEntry(userInput, response));
        log.debug("[SemanticCache] 写入缓存: key={}", key.substring(0, 16));
    }

    /**
     * 使缓存失效（可用于用户要求刷新回答时）
     */
    public void invalidate(String systemPrompt, String userInput, String modelName) {
        String key = buildExactKey(systemPrompt, userInput, modelName);
        exactCache.invalidate(key);
    }

    /**
     * 清空所有缓存
     */
    public void clear() {
        exactCache.invalidateAll();
    }

    /**
     * 获取缓存统计信息
     */
    public String getStats() {
        return exactCache.stats().toString();
    }

    // ========== 私有方法 ==========

    private String buildExactKey(String systemPrompt, String userInput, String modelName) {
        return DigestUtil.md5Hex(
                StrUtil.nullToDefault(systemPrompt, "") + "|"
              + StrUtil.nullToDefault(modelName, "") + "|"
              + StrUtil.nullToDefault(userInput, ""));
    }

    /**
     * 缓存条目
     */
    static class CacheEntry {
        private final String question;
        private final String response;
        private final long createTime;

        CacheEntry(String question, String response) {
            this.question = question;
            this.response = response;
            this.createTime = System.currentTimeMillis();
        }

        public String getQuestion() { return question; }
        public String getResponse() { return response; }
        public long getCreateTime() { return createTime; }
    }
}
