package cn.iocoder.yudao.module.ai.framework.ai.core.agent.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * Agent 工具箱 — 统一管理所有可用工具
 *
 * <p>从 Spring 容器自动发现所有 {@link ToolCallback} 实现，
 * 同时支持通过 {@link ToolCallbackResolver} 动态解析工具。</p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AgentToolbox {

    @Resource
    private ToolCallbackResolver toolCallbackResolver;

    @Resource
    private ApplicationContext applicationContext;

    /** 缓存已解析的工具 */
    private volatile List<ToolCallback> cachedTools;
    private volatile long lastRefreshTime = 0;
    private static final long CACHE_TTL_MS = 60_000; // 1分钟缓存

    /**
     * 获取所有可用工具（带缓存）
     */
    public List<ToolCallback> getAllTools() {
        if (cachedTools != null && System.currentTimeMillis() - lastRefreshTime < CACHE_TTL_MS) {
            return new ArrayList<>(cachedTools);
        }
        synchronized (this) {
            if (cachedTools != null && System.currentTimeMillis() - lastRefreshTime < CACHE_TTL_MS) {
                return new ArrayList<>(cachedTools);
            }
            cachedTools = discoverTools();
            lastRefreshTime = System.currentTimeMillis();
            return new ArrayList<>(cachedTools);
        }
    }

    /**
     * 从 Spring 容器中发现所有工具
     */
    private List<ToolCallback> discoverTools() {
        Set<ToolCallback> tools = new LinkedHashSet<>();

        // 方式1: 从 Spring 容器中获取所有 ToolCallback bean
        try {
            Map<String, ToolCallback> toolBeans = applicationContext.getBeansOfType(ToolCallback.class);
            tools.addAll(toolBeans.values());
            log.debug("[AgentToolbox] 从容器发现 {} 个 ToolCallback bean", toolBeans.size());
        } catch (Exception e) {
            log.debug("[AgentToolbox] 从容器获取 ToolCallback bean 失败: {}", e.getMessage());
        }

        // 方式2: 通过 ToolCallbackResolver 解析已知工具名
        try {
            // 从容器中获取所有 Function bean 作为备选工具名
            String[] functionBeanNames = applicationContext.getBeanNamesForType(java.util.function.Function.class);
            for (String name : functionBeanNames) {
                try {
                    ToolCallback tool = toolCallbackResolver.resolve(name);
                    if (tool != null) {
                        tools.add(tool);
                    }
                } catch (Exception ignored) {
                    // 无法解析则跳过
                }
            }
        } catch (Exception e) {
            log.debug("[AgentToolbox] 通过 ToolCallbackResolver 发现工具: {}", e.getMessage());
        }

        // 方式3: 尝试已知工具名
        String[] knownToolNames = {
                "weather_query", "weatherQueryToolFunction",
                "user_profile_query", "userProfileQueryToolFunction",
                "directory_list", "directoryListToolFunction",
                "personService"
        };
        for (String name : knownToolNames) {
            try {
                ToolCallback tool = toolCallbackResolver.resolve(name);
                if (tool != null) {
                    tools.add(tool);
                }
            } catch (Exception ignored) {
                // 工具不存在则跳过
            }
        }

        if (tools.isEmpty()) {
            log.warn("[AgentToolbox] 未发现任何可用工具，Agent 将只能进行纯文本推理");
        } else {
            log.info("[AgentToolbox] 共发现 {} 个可用工具: {}", tools.size(),
                    tools.stream().map(t -> t.getToolDefinition().name()).toList());
        }

        return new ArrayList<>(tools);
    }

    /**
     * 按名称获取工具
     */
    public Optional<ToolCallback> getTool(String name) {
        if (name == null) return Optional.empty();

        // 先尝试精确匹配
        for (ToolCallback tool : getAllTools()) {
            if (name.equals(tool.getToolDefinition().name())) {
                return Optional.of(tool);
            }
        }

        // 尝试模糊匹配（忽略大小写和下划线/驼峰差异）
        String normalized = normalize(name);
        for (ToolCallback tool : getAllTools()) {
            if (normalized.equals(normalize(tool.getToolDefinition().name()))) {
                return Optional.of(tool);
            }
        }

        // 尝试通过 ToolCallbackResolver 动态解析
        try {
            ToolCallback resolved = toolCallbackResolver.resolve(name);
            if (resolved != null) return Optional.of(resolved);
        } catch (Exception ignored) {}

        return Optional.empty();
    }

    /**
     * 获取所有工具名称列表
     */
    public List<String> getToolNames() {
        return getAllTools().stream()
                .map(t -> t.getToolDefinition().name())
                .toList();
    }

    /**
     * 获取工具描述（给 LLM 看的）
     */
    public String getToolsDescription() {
        List<ToolCallback> tools = getAllTools();
        if (tools.isEmpty()) return "无可用工具";

        StringBuilder sb = new StringBuilder("可用工具：\n");
        for (ToolCallback tool : tools) {
            sb.append("- ").append(tool.getToolDefinition().name())
                    .append(": ").append(tool.getToolDefinition().description())
                    .append("\n");
        }
        return sb.toString();
    }

    /**
     * 刷新工具缓存
     */
    public void refresh() {
        synchronized (this) {
            cachedTools = null;
            lastRefreshTime = 0;
        }
    }

    /**
     * 字符串标准化（去下划线、转小写）
     */
    private static String normalize(String s) {
        return s.replace("_", "").toLowerCase();
    }
}
