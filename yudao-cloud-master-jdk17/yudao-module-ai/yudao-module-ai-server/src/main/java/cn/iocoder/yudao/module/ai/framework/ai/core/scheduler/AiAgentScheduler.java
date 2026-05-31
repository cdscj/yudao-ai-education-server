package cn.iocoder.yudao.module.ai.framework.ai.core.scheduler;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.memory.MemoryAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgentTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class AiAgentScheduler {

    private final Map<String, AiAgent> agentMap = new ConcurrentHashMap<>();

    @Resource
    private MemoryAgent memoryAgent;

    public AiAgentScheduler(ApplicationContext context) {
        Map<String, AiAgent> agents = context.getBeansOfType(AiAgent.class);
        agents.values().forEach(agent -> agentMap.put(agent.getAgentId(), agent));
        log.info("[scheduler] 已注册 {} 个智能体: {}", agentMap.size(), agentMap.keySet());
    }

    public AiAgent getAgent(String agentId) {
        AiAgent agent = agentMap.get(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("未找到智能体: " + agentId);
        }
        return agent;
    }

    public AiAgent getAgentByType(AiAgentTypeEnum type) {
        return getAgent(type.getCode() + "-agent");
    }

    public Flux<String> executeAgent(String agentId, String input, String context) {
        AiAgent agent = getAgent(agentId);
        log.info("[scheduler] 执行智能体: {} ({}), input长度: {}", agent.getAgentName(), agentId, input.length());
        return agent.executeStream(input, context);
    }

    public Flux<String> executeWithMemory(String agentId, String input, String history) {
        String compressedContext = memoryAgent.compressSync(history);
        return executeAgent(agentId, input, compressedContext);
    }

    public String getAgentSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("当前注册智能体(").append(agentMap.size()).append("个):\n");
        agentMap.values().forEach(a -> sb.append("- ").append(a.getAgentName())
                .append(" (").append(a.getAgentId()).append(")\n"));
        return sb.toString();
    }

    public Map<String, String> getAllAgentInfo() {
        Map<String, String> info = new ConcurrentHashMap<>();
        agentMap.values().forEach(a -> info.put(a.getAgentId(), a.getAgentName()));
        return info;
    }
}
