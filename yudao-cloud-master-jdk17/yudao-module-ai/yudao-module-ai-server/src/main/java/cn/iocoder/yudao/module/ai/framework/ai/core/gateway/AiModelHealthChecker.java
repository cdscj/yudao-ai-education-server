package cn.iocoder.yudao.module.ai.framework.ai.core.gateway;

import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.AiModelFactory;
import cn.iocoder.yudao.module.ai.service.model.AiApiKeyService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI 模型健康检查器 — 定时探测各模型的可用性
 *
 * <p>每分钟检查一次各启用模型的状态，标记健康/不健康。
 * 不健康的模型会被 Gateway 在路由时自动跳过。</p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AiModelHealthChecker {

    @Resource
    private AiModelService modelService;
    @Resource
    private AiApiKeyService apiKeyService;
    @Resource
    private AiModelFactory modelFactory;

    /**
     * 模型健康状态 (key: modelId, value: true=健康)
     */
    private final ConcurrentHashMap<Long, Boolean> healthStatus = new ConcurrentHashMap<>();

    /**
     * 每分钟检查一次模型健康状态
     */
    @Scheduled(fixedDelay = 60_000, initialDelay = 30_000)
    public void checkHealth() {
        // 获取所有启用的 Chat 模型
        List<AiModelDO> models = modelService.getEnabledModels(1); // 1 = CHAT type
        for (AiModelDO model : models) {
            try {
                AiApiKeyDO apiKey = apiKeyService.getApiKey(model.getKeyId());
                if (apiKey == null) {
                    healthStatus.put(model.getId(), false);
                    continue;
                }
                // 简单探测：尝试获取模型实例（不实际调用）
                AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
                modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), apiKey.getUrl());
                healthStatus.put(model.getId(), true);
            } catch (Exception e) {
                log.warn("[HealthCheck] 模型 {} (id={}) 健康检查失败: {}",
                        model.getName(), model.getId(), e.getMessage());
                healthStatus.put(model.getId(), false);
            }
        }
    }

    public boolean isHealthy(Long modelId) {
        return healthStatus.getOrDefault(modelId, true); // 未检查过的默认健康
    }

    public Map<Long, Boolean> getAllHealthStatus() {
        return new ConcurrentHashMap<>(healthStatus);
    }
}
