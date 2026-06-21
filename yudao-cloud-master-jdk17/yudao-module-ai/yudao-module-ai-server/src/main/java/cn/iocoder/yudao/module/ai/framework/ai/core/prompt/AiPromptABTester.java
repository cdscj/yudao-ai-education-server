package cn.iocoder.yudao.module.ai.framework.ai.core.prompt;

import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiPromptTemplateDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiPromptTemplateMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * AI Prompt A/B 测试分流器
 *
 * <p>按 userId 的 hash 值将流量分配到 A/B 两组，
 * 每组使用不同版本的 Prompt 模板，从而对比效果。</p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AiPromptABTester {

    @Resource
    private AiPromptTemplateMapper promptTemplateMapper;

    /**
     * 根据用户分流选择 Prompt 模板
     *
     * @param templateKey 模板标识（code/name）
     * @param userId      用户ID（用于 hash 分流）
     * @return 选中的模板，无 A/B 测试时返回默认模板
     */
    public AiPromptTemplateDO selectTemplate(String templateKey, Long userId) {
        // 查找该 key 下的所有 A/B 测试模板
        List<AiPromptTemplateDO> templates = promptTemplateMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiPromptTemplateDO>()
                        .eq(AiPromptTemplateDO::getCode, templateKey)
                        .eq(AiPromptTemplateDO::getStatus, 0)); // ENABLE

        if (templates.isEmpty()) return null;

        // 如果没有 A/B 分组，返回默认
        List<AiPromptTemplateDO> abTemplates = templates.stream()
                .filter(t -> t.getAbGroup() != null)
                .toList();

        if (abTemplates.isEmpty()) {
            return templates.get(0);
        }

        // hash 分流
        int bucket = Math.abs(userId.hashCode() % 100);
        AiPromptTemplateDO selected;
        if (bucket < 50) {
            selected = abTemplates.stream()
                    .filter(t -> "A".equals(t.getAbGroup()))
                    .findFirst().orElse(templates.get(0));
        } else {
            selected = abTemplates.stream()
                    .filter(t -> "B".equals(t.getAbGroup()))
                    .findFirst().orElse(templates.get(0));
        }

        log.debug("[ABTester] 模板={}, userId={}, bucket={}, 选中组={}, 版本=v{}",
                templateKey, userId, bucket,
                selected.getAbGroup(), selected.getVersion());
        return selected;
    }
}
