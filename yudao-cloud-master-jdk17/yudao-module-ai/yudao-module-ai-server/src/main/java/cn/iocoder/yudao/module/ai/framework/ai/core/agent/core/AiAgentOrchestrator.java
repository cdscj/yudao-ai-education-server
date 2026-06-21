package cn.iocoder.yudao.module.ai.framework.ai.core.agent.core;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.router.AgentRouter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * 多 Agent 编排器 — 支持串行链和辩论模式
 *
 * <p>编排模式：
 * <ul>
 *   <li><b>ROUTE</b>: 根据意图路由到单个 Agent</li>
 *   <li><b>CHAIN</b>: Agent A 输出 → Agent B 输入（串行链）</li>
 *   <li><b>DEBATE</b>: 两个 Agent 互相 critique 后合成最终答案</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
@Service
@Slf4j
public class AiAgentOrchestrator {

    @Resource
    private AgentRouter router;

    /**
     * 路由模式：根据意图自动选择 Agent 执行
     */
    public Flux<String> routeAndExecute(String input, String context) {
        AiAgent agent = router.route(input);
        if (agent == null) {
            return Flux.just("【没有可用的 Agent 处理你的请求】");
        }
        log.info("[Orchestrator] 路由到 Agent: {} ({})", agent.getAgentName(), agent.getAgentId());
        return agent.execute(input, context);
    }

    /**
     * 串行链模式：Agent A 处理 → Agent B 基于 A 的输出继续处理
     */
    public Flux<String> chainAndExecute(AiAgent first, AiAgent second,
                                         String input, String context) {
        return first.execute(input, context)
                .collectList()
                .flatMapMany(results -> {
                    String intermediateResult = String.join("", results);
                    String newInput = input + "\n\n[上一步Agent的处理结果]\n" + intermediateResult;
                    return second.execute(newInput, context);
                });
    }
}
