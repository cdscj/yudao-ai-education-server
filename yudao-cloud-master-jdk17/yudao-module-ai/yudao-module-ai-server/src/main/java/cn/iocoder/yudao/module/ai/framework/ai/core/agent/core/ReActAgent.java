package cn.iocoder.yudao.module.ai.framework.ai.core.agent.core;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

/**
 * ReAct (Reasoning + Acting) Agent 执行器
 *
 * <p>实现 think → act → observe 循环：
 * <pre>
 *   1. Think: LLM 分析当前状态，决定下一步行动
 *   2. Act: 执行选定的工具或给出最终答案
 *   3. Observe: 获取工具执行结果，更新记忆
 *   4. 重复 1-3 直到获得最终答案或达到最大步数
 * </pre>
 * </p>
 *
 * @author yudao
 */
@Slf4j
public abstract class ReActAgent implements AiAgent {

    @Resource
    protected AiLlmService llmService;
    @Resource
    protected AgentToolbox toolbox;

    /** 最大迭代步数 */
    private static final int MAX_STEPS = 10;
    /** 单步超时 */
    private static final Duration STEP_TIMEOUT = Duration.ofSeconds(30);

    @Override
    public Flux<String> execute(String input, String context) {
        return executeStream(input, context);
    }

    @Override
    public Flux<String> executeStream(String input, String context) {
        return Flux.defer(() -> {
            AgentMemory memory = new AgentMemory();

            // 步骤 1: 初始思考
            String thought = think(input, context, memory);
            memory.addObservation(Observation.of(1, thought));

            // 如果初始思考就是最终答案
            if (isFinalAnswer(thought)) {
                memory.setFinalAnswer(extractAnswer(thought));
                return Flux.just(memory.getFinalAnswer());
            }

            // 步骤 2-N: ReAct 循环
            return Flux.using(
                    () -> new ReActState(memory, 2, input, context),
                    state -> executeLoop(state),
                    state -> log.debug("[ReAct] 完成, 共 {} 步", state.memory.getStepCount())
            );
        });
    }

    private Flux<String> executeLoop(ReActState state) {
        return Flux.generate(
                () -> state,
                (s, sink) -> {
                    if (s.step > MAX_STEPS) {
                        s.memory.setFinalAnswer("【ReAct 达到最大步数限制，返回当前分析结果】");
                        sink.next(s.memory.getFinalAnswer());
                        sink.complete();
                        return s;
                    }

                    try {
                        String thought = think(s.input, s.context, s.memory);
                        Observation obs = Observation.of(s.step, thought);
                        s.memory.addObservation(obs);

                        if (isFinalAnswer(thought)) {
                            String answer = extractAnswer(thought);
                            s.memory.setFinalAnswer(answer);
                            sink.next(answer);
                            sink.complete();
                        } else {
                            // 中间步骤，输出思考过程（可选）
                            sink.next("[思考] " + thought + "\n");
                            s.step++;
                        }
                    } catch (Exception e) {
                        log.error("[ReAct] 步骤 {} 异常", s.step, e);
                        s.memory.setFinalAnswer("【ReAct 执行出错: " + e.getMessage() + "】");
                        sink.next(s.memory.getFinalAnswer());
                        sink.complete();
                    }
                    return s;
                }
        );
    }

    // ========== 子类需实现的方法 ==========

    /**
     * 获取 Agent 的系统提示词
     */
    protected abstract String getSystemPrompt();

    /**
     * 思考下一步行动
     */
    protected String think(String input, String context, AgentMemory memory) {
        String toolsDesc = toolbox.getToolsDescription();
        String historyContext = memory.getContext();

        String prompt = String.format("""
                %s

                %s

                对话上下文：%s

                你的思考历史：
                %s

                用户输入：%s

                请决定下一步：
                - 如果可以使用工具解决问题，输出: TOOL: <工具名>(<参数>)
                - 如果可以给出最终答案，输出: FINAL: <你的回答>
                """, getSystemPrompt(), toolsDesc,
                context != null ? context : "无",
                historyContext.isEmpty() ? "无" : historyContext, input);

        try {
            return llmService.chatSync(getSystemPrompt(), prompt, context);
        } catch (Exception e) {
            log.error("[ReAct] think 调用失败", e);
            return "FINAL: 抱歉，我暂时无法处理你的请求，请稍后重试。";
        }
    }

    // ========== 解析辅助方法 ==========

    protected boolean isFinalAnswer(String thought) {
        return thought != null && (
                thought.trim().startsWith("FINAL:")
             || thought.trim().startsWith("最终答案:")
             || !thought.contains("TOOL:")
        );
    }

    protected String extractAnswer(String thought) {
        if (thought == null) return "";
        String[] markers = {"FINAL:", "最终答案:"};
        for (String marker : markers) {
            int idx = thought.indexOf(marker);
            if (idx >= 0) {
                return thought.substring(idx + marker.length()).trim();
            }
        }
        return thought.trim();
    }

    private static class ReActState {
        final AgentMemory memory;
        int step;
        final String input;
        final String context;

        ReActState(AgentMemory memory, int step, String input, String context) {
            this.memory = memory;
            this.step = step;
            this.input = input;
            this.context = context;
        }
    }
}
