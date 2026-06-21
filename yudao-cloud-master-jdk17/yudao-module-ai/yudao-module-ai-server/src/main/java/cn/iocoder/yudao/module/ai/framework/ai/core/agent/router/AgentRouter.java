package cn.iocoder.yudao.module.ai.framework.ai.core.agent.router;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgentTypeEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.core.ReActAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Agent 路由器 — 根据用户意图自动分发到最合适的 Agent
 *
 * <p>使用轻量级 LLM 调用进行意图分类，然后路由到对应的专业 Agent。</p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AgentRouter {

    @Resource
    private AiLlmService llmService;

    /** 所有注册的 Agent (key: agentId) */
    private Map<String, AiAgent> agentMap = Map.of();

    public AgentRouter(List<AiAgent> agents) {
        this.agentMap = agents.stream()
                .collect(Collectors.toMap(AiAgent::getAgentId, Function.identity()));
    }

    private static final String CLASSIFY_PROMPT = """
            你是一个意图分类助手。根据用户输入，判断应该使用哪个Agent：
            - code-agent: 写代码、编程、调试、算法问题
            - research-agent: 研究分析、查找资料、深度调查
            - write-agent: 写作、翻译、润色、文案
            - tutor-agent: 学习辅导、答疑、课程相关
            - general: 其他一般问题

            只输出 agent-id，不要解释。""";

    /**
     * 路由到最合适的 Agent
     */
    public AiAgent route(String userInput) {
        if (agentMap.isEmpty()) {
            return null;
        }

        try {
            String agentId = llmService.chatSync(CLASSIFY_PROMPT,
                    "用户输入：" + userInput + "\n请给出最合适的agent-id：", "");
            if (agentId != null) {
                agentId = agentId.trim().toLowerCase();
                AiAgent agent = agentMap.get(agentId);
                if (agent != null) {
                    log.debug("[Router] 路由到 Agent: {}", agentId);
                    return agent;
                }
            }
        } catch (Exception e) {
            log.warn("[Router] 意图分类失败，使用默认Agent: {}", e.getMessage());
        }

        // 默认返回通用 Agent
        return agentMap.values().stream().findFirst().orElse(null);
    }
}
