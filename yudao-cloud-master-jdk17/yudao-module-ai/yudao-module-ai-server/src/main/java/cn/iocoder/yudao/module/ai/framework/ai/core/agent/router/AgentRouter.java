package cn.iocoder.yudao.module.ai.framework.ai.core.agent.router;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
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
 * <p>使用轻量级 LLM 调用进行意图分类，然后路由到对应的专业 Agent。
 * 支持通用 Agent（code/research/write/tutor/general）和
 * 教育专用 Agent（profile/resource/path/education-workflow）。</p>
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
        log.info("[AgentRouter] 已注册 {} 个 Agent: {}", agents.size(),
                agents.stream().map(a -> a.getAgentId() + "(" + a.getAgentName() + ")").toList());
    }

    private static final String CLASSIFY_PROMPT = """
            你是一个意图分类助手。根据用户输入，判断应该使用哪个Agent：

            通用Agent：
            - code-agent: 写代码、编程、调试、算法问题
            - research-agent: 研究分析、查找资料、深度调查
            - write-agent: 写作、翻译、润色、文案
            - tutor-agent: 学习辅导、答疑、课程相关
            - general: 其他一般问题

            教育专用Agent：
            - profile-agent: 学生画像构建、学习情况分析、知识水平评估
            - resource-agent: 学习资源生成、文档/PPT/习题/代码案例生成
            - path-agent: 学习路径规划、学习计划安排、课程推荐
            - education-workflow: 完整学习方案生成（画像+资源+路径一站式服务）

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

        // 检查是否为教育相关输入，优先使用教育协同工作流
        String lower = userInput.toLowerCase();
        if (containsAny(lower, "学习", "课程", "教程", "知识点", "习题", "考试",
                "学习路径", "学习计划", "画像", "资源生成")) {
            AiAgent eduAgent = agentMap.get("education-workflow");
            if (eduAgent != null) {
                log.debug("[Router] 教育关键词匹配，路由到 education-workflow");
                return eduAgent;
            }
        }

        // 默认返回通用 Agent
        return agentMap.values().stream().findFirst().orElse(null);
    }

    /**
     * 按 ID 获取指定 Agent
     */
    public AiAgent getAgentById(String agentId) {
        return agentMap.get(agentId);
    }

    /**
     * 获取所有已注册的 Agent ID
     */
    public List<String> getRegisteredAgentIds() {
        return List.copyOf(agentMap.keySet());
    }

    /**
     * 获取 Agent 总数
     */
    public int getAgentCount() {
        return agentMap.size();
    }

    private static boolean containsAny(String text, String... keywords) {
        for (String kw : keywords) {
            if (text.contains(kw)) return true;
        }
        return false;
    }
}
