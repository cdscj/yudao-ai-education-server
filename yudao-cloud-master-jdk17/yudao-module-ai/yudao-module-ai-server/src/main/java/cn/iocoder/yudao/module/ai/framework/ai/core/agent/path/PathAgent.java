package cn.iocoder.yudao.module.ai.framework.ai.core.agent.path;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyFilter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class PathAgent implements AiAgent {

    @Resource
    private AiLlmService llmService;
    @Resource
    private AiSafetyFilter safetyFilter;

    private static final String SYSTEM_PROMPT = """
            你是学习路径规划智能体。根据学生的学习目标、课程和时间周期，生成个性化学习路径。
            路径应按阶段（周/月）划分，每个阶段包含：学习目标、核心知识点、推荐资源类型、预期成果。
            路径需由浅入深，符合认知规律，并预留复习和练习时间。
            """;

    @Override
    public String getAgentId() { return "path-agent"; }

    @Override
    public String getAgentName() { return "路径规划智能体"; }

    @Override
    public Flux<String> execute(String input, String context) {
        return executeStream(input, context);
    }

    @Override
    public Flux<String> executeStream(String input, String context) {
        String safeInput = safetyFilter.filter(input);
        return llmService.chatStream(SYSTEM_PROMPT, safeInput, context)
                .map(safetyFilter::filterOutput);
    }
}
