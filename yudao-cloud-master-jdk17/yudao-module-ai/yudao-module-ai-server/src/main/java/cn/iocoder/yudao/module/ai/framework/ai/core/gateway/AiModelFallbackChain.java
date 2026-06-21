package cn.iocoder.yudao.module.ai.framework.ai.core.gateway;

import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI 模型 Fallback 链执行器
 *
 * <p>当主模型不可用时，按优先级依次尝试备用模型：
 * <ol>
 *   <li>同类型（平台）的其他启用模型</li>
 *   <li>配置的兜底模型</li>
 *   <li>静态兜底回复</li>
 * </ol>
 * </p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AiModelFallbackChain {

    @Resource
    private AiModelService modelService;
    @Resource
    private AiModelRouteConfig routeConfig;

    /**
     * 获取 fallback 模型列表（按优先级排序）
     *
     * @param type            模型类型（1=Chat, 2=Image, ...）
     * @param excludeModelId  排除的模型ID（即已失败的模型）
     * @return fallback 模型列表
     */
    public List<AiModelDO> getFallbackModels(Integer type, Long excludeModelId) {
        List<AiModelDO> models = modelService.getFallbackModels(type, excludeModelId);
        // 如果配置了兜底模型ID，将其排在最后
        if (routeConfig.getFallback().getDefaultModelId() != null) {
            AiModelDO defaultModel = modelService.getModel(
                    routeConfig.getFallback().getDefaultModelId());
            if (defaultModel != null && !defaultModel.getId().equals(excludeModelId)
                    && models.stream().noneMatch(m -> m.getId().equals(defaultModel.getId()))) {
                models.add(defaultModel);
            }
        }
        return models;
    }

    public boolean isFallbackEnabled() {
        return routeConfig.getFallback().isEnabled();
    }
}
