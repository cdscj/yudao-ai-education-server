package cn.iocoder.yudao.module.ai.framework.ai.core.prompt;

import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiPromptTemplateDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiPromptTemplateMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * AI Prompt 版本管理器 — 管理模板的版本链
 *
 * <p>每次修改模板时自动递增版本号，旧版本保留为历史记录，
 * 支持回滚到任意历史版本。</p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AiPromptVersionManager {

    @Resource
    private AiPromptTemplateMapper promptTemplateMapper;

    /**
     * 创建新版本（版本号自增）
     *
     * @param template 模板（version 会被自动设置）
     */
    public void createNewVersion(AiPromptTemplateDO template) {
        // 查询当前最大版本号
        List<AiPromptTemplateDO> history = getVersionHistory(template.getParentId() != null
                ? template.getParentId() : template.getId());
        int maxVersion = history.stream()
                .mapToInt(t -> t.getVersion() != null ? t.getVersion() : 1)
                .max().orElse(0);
        template.setVersion(maxVersion + 1);
        log.info("[VersionManager] 模板 {} 创建版本 v{}", template.getName(), template.getVersion());
    }

    /**
     * 获取模板的所有历史版本
     */
    public List<AiPromptTemplateDO> getVersionHistory(Long templateId) {
        // 查找根模板（parent_id IS NULL）
        AiPromptTemplateDO root = promptTemplateMapper.selectById(templateId);
        if (root == null) return List.of();
        Long rootId = root.getParentId() != null ? root.getParentId() : root.getId();
        // 返回所有 parent_id = rootId 或 id = rootId 的版本
        List<AiPromptTemplateDO> versions = promptTemplateMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiPromptTemplateDO>()
                        .eq(AiPromptTemplateDO::getId, rootId)
                        .or()
                        .eq(AiPromptTemplateDO::getParentId, rootId)
                        .orderByDesc(AiPromptTemplateDO::getVersion));
        return versions;
    }

    /**
     * 获取指定版本
     */
    public AiPromptTemplateDO getVersion(Long rootId, int version) {
        if (version <= 0) return promptTemplateMapper.selectById(rootId);
        return promptTemplateMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiPromptTemplateDO>()
                        .eq(AiPromptTemplateDO::getParentId, rootId)
                        .eq(AiPromptTemplateDO::getVersion, version));
    }
}
