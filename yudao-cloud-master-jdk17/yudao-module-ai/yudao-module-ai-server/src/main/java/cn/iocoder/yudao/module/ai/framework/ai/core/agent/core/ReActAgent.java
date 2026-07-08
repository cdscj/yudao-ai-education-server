package cn.iocoder.yudao.module.ai.framework.ai.core.agent.core;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ReAct (Reasoning + Acting) Agent 执行器
 *
 * <p>实现完整的 think → act → observe 循环：
 * <pre>
 *   1. Think: LLM 分析当前状态，决定下一步行动
 *   2. Act: 解析工具调用，执行选定的工具
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
    AiLlmService llmService;
    @Resource
    AgentToolbox toolbox;

    /** 最大迭代步数 */
    private static final int MAX_STEPS = 10;
    /** 单步超时 */
    private static final Duration STEP_TIMEOUT = Duration.ofSeconds(30);

    /** 匹配 TOOL: toolName(params) 或 TOOL: toolName */
    private static final Pattern TOOL_PATTERN =
            Pattern.compile("TOOL:\\s*(\\S+)\\s*(?:\\(\\s*(.*?)\\s*\\))?", Pattern.CASE_INSENSITIVE);

    /** 匹配 FINAL: answer 或 最终答案: answer */
    private static final Pattern FINAL_PATTERN =
            Pattern.compile("(?:FINAL|最终答案)\\s*[:：]\\s*(.+)", Pattern.CASE_INSENSITIVE);

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

            // 步骤 2-N: ReAct 循环（Think → Act → Observe）
            return Flux.using(
                    () -> new ReActState(memory, 2, input, context),
                    state -> executeLoop(state),
                    state -> log.debug("[ReAct] 完成, 共 {} 步", state.memory.getStepCount())
            );
        });
    }

    /**
     * 执行 ReAct 主循环：Think → Act → Observe
     */
    private Flux<String> executeLoop(ReActState state) {
        return Flux.generate(
                () -> state,
                (s, sink) -> {
                    if (s.step > MAX_STEPS) {
                        String fallback = buildFallbackAnswer(s.memory);
                        s.memory.setFinalAnswer(fallback);
                        sink.next(fallback);
                        sink.complete();
                        return s;
                    }

                    try {
                        // ===== Think 阶段 =====
                        String thought = think(s.input, s.context, s.memory);
                        Observation obs = Observation.of(s.step, thought);

                        if (isFinalAnswer(thought)) {
                            // 获得最终答案
                            String answer = extractAnswer(thought);
                            obs.setObservation("任务完成，给出最终答案");
                            s.memory.addObservation(obs);
                            s.memory.setFinalAnswer(answer);
                            sink.next(answer);
                            sink.complete();
                            return s;
                        }

                        // ===== Act 阶段：解析并执行工具调用 =====
                        ParsedToolCall toolCall = parseToolCall(thought);
                        if (toolCall != null) {
                            sink.next("[步骤" + s.step + "] 调用工具: " + toolCall.toolName + "\n");

                            Observation.Action action = new Observation.Action();
                            action.setToolName(toolCall.toolName);
                            action.setToolInput(toolCall.toolInput);
                            obs.setAction(action);

                            // 执行工具
                            String toolResult = executeTool(toolCall.toolName, toolCall.toolInput);
                            obs.setObservation(toolResult);
                            sink.next("[工具结果] " + truncate(toolResult, 200) + "\n");
                        } else {
                            // 未解析到工具调用，标记为思考步骤
                            obs.setObservation("继续思考中...");
                            sink.next("[思考] " + truncate(thought, 300) + "\n");
                        }

                        s.memory.addObservation(obs);
                        s.step++;
                    } catch (Exception e) {
                        log.error("[ReAct] 步骤 {} 异常", s.step, e);
                        String fallback = "【ReAct 执行出错: " + e.getMessage()
                                + "。已完成 " + s.memory.getStepCount() + " 步分析】";
                        s.memory.setFinalAnswer(fallback);
                        sink.next(fallback);
                        sink.complete();
                    }
                    return s;
                }
        );
    }

    // ========== 工具调用解析与执行 ==========

    /**
     * 从思考内容中解析工具调用
     *
     * @param thought LLM 的思考输出
     * @return 解析出的工具调用，如果没有则返回 null
     */
    private ParsedToolCall parseToolCall(String thought) {
        if (thought == null) return null;

        Matcher matcher = TOOL_PATTERN.matcher(thought);
        if (matcher.find()) {
            String toolName = matcher.group(1).trim();
            String toolInput = matcher.group(2) != null ? matcher.group(2).trim() : "";
            return new ParsedToolCall(toolName, toolInput);
        }
        return null;
    }

    /**
     * 执行工具并返回结果
     *
     * @param toolName  工具名称
     * @param toolInput 工具输入参数
     * @return 工具执行结果
     */
    private String executeTool(String toolName, String toolInput) {
        Optional<ToolCallback> toolOpt = toolbox.getTool(toolName);
        if (toolOpt.isEmpty()) {
            return "工具 [" + toolName + "] 未找到。可用工具: " + toolbox.getToolNames();
        }

        try {
            ToolCallback tool = toolOpt.get();
            String result = tool.call(toolInput);
            log.debug("[ReAct] 工具 {} 执行成功, 输入: {}", toolName, toolInput);
            return result != null ? result : "工具执行完成（无返回内容）";
        } catch (Exception e) {
            log.error("[ReAct] 工具 {} 执行失败, 输入: {}", toolName, toolInput, e);
            return "工具 [" + toolName + "] 执行失败: " + e.getMessage();
        }
    }

    /**
     * 构建兜底回答（达到最大步数时）
     */
    private String buildFallbackAnswer(AgentMemory memory) {
        StringBuilder sb = new StringBuilder();
        sb.append("【经过 ").append(MAX_STEPS).append(" 步分析，以下是当前结论】\n\n");

        // 汇总所有观察结果
        for (Observation obs : memory.getObservations()) {
            if (obs.getObservation() != null && !obs.getObservation().isEmpty()
                    && !obs.getObservation().equals("继续思考中...")) {
                sb.append("- ").append(truncate(obs.getObservation(), 500)).append("\n");
            }
        }

        if (memory.getObservations().stream().allMatch(o -> o.getObservation() == null
                || o.getObservation().isEmpty()
                || o.getObservation().equals("继续思考中..."))) {
            sb.append("抱歉，在有限步骤内未能完成分析。请尝试更具体地描述你的问题。");
        }

        return sb.toString();
    }

    // ========== 子类需实现的方法 ==========

    /**
     * 获取 Agent 的系统提示词
     */
    public abstract String getSystemPrompt();

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
                - 如果需要使用工具来获取信息或执行操作，输出: TOOL: <工具名>(<参数>)
                - 如果已经可以给出最终答案，输出: FINAL: <你的回答>
                - 工具调用示例: TOOL: weather_query(city=北京)
                - 最终答案示例: FINAL: 根据分析结果...
                """, getSystemPrompt(), toolsDesc,
                context != null ? context : "无",
                historyContext.isEmpty() ? "（首次思考）" : historyContext, input);

        try {
            return llmService.chatSync(getSystemPrompt(), prompt, context);
        } catch (Exception e) {
            log.error("[ReAct] think 调用失败", e);
            return "FINAL: 抱歉，我暂时无法处理你的请求，请稍后重试。";
        }
    }

    // ========== 解析辅助方法 ==========

    protected boolean isFinalAnswer(String thought) {
        if (thought == null) return false;
        String trimmed = thought.trim();
        return FINAL_PATTERN.matcher(trimmed).find()
                || (!trimmed.toUpperCase().contains("TOOL:") && trimmed.length() > 20);
    }

    protected String extractAnswer(String thought) {
        if (thought == null) return "";
        Matcher matcher = FINAL_PATTERN.matcher(thought);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        // 如果没有显式标记但也没有工具调用，把整个thought当作答案
        return thought.trim();
    }

    // ========== 工具方法 ==========

    private static String truncate(String text, int maxLen) {
        if (text == null) return "";
        return text.length() <= maxLen ? text : text.substring(0, maxLen) + "...";
    }

    /**
     * 解析出的工具调用
     */
    private record ParsedToolCall(String toolName, String toolInput) {
    }

    /**
     * ReAct 循环状态
     */
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
