package cn.iocoder.yudao.module.ai.framework.ai.core.agent.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.resolution.ToolCallbackResolver;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.*;

/**
 * Agent 工具箱 — 统一管理所有可用工具
 *
 * @author yudao
 */
@Component
@Slf4j
public class AgentToolbox {

    @Resource
    private ToolCallbackResolver toolCallbackResolver;

    /**
     * 获取所有可用工具
     */
    public List<ToolCallback> getAllTools() {
        // 从 Spring 容器获取所有 ToolCallback
        Map<String, ToolCallback> allTools = new HashMap<>();
        try {
            // 尝试常见的工具名
            String[] knownTools = {"weatherQueryToolFunction",
                    "userProfileQueryToolFunction", "directoryListToolFunction",
                    "personService"};
            for (String name : knownTools) {
                ToolCallback tool = toolCallbackResolver.resolve(name);
                if (tool != null) {
                    allTools.put(name, tool);
                }
            }
        } catch (Exception e) {
            log.warn("[AgentToolbox] 获取工具列表失败: {}", e.getMessage());
        }
        return new ArrayList<>(allTools.values());
    }

    /**
     * 按名称获取工具
     */
    public Optional<ToolCallback> getTool(String name) {
        ToolCallback tool = toolCallbackResolver.resolve(name);
        return Optional.ofNullable(tool);
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
}
