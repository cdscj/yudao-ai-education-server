package cn.iocoder.yudao.module.ai.framework.ai.core.agent.resource;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyFilter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class ResourceAgent implements AiAgent {

    @Resource
    private AiLlmService llmService;
    @Resource
    private AiSafetyFilter safetyFilter;

    private static final String SYSTEM_PROMPT_TEMPLATE = """
            你是多模态学习资源生成智能体。根据用户主题和资源类型生成优质内容。
            支持的资源类型：DOCUMENT(课程文档)、MIND_MAP(思维导图)、EXERCISE(分层练习题)、READING(拓展阅读)、CODE_EXAMPLE(代码实操)
            
            当前类型：%s
            主题：%s
            难度：%s
            
            要求：内容准确、结构清晰、符合教学规范。
            """;

    @Override
    public String getAgentId() { return "resource-agent"; }

    @Override
    public String getAgentName() { return "资源生成智能体"; }

    @Override
    public Flux<String> execute(String input, String context) {
        return executeStream(input, context);
    }

    @Override
    public Flux<String> executeStream(String input, String context) {
        String safeInput = safetyFilter.filter(input);
        String systemPrompt = String.format(SYSTEM_PROMPT_TEMPLATE,
                extractType(input), extractTopic(input), extractDifficulty(input));
        return llmService.chatStream(systemPrompt, safeInput, context)
                .map(safetyFilter::filterOutput);
    }

    private String extractType(String input) {
        if (input.contains("MIND_MAP")) return "MIND_MAP";
        if (input.contains("EXERCISE")) return "EXERCISE";
        if (input.contains("READING")) return "READING";
        if (input.contains("CODE_EXAMPLE")) return "CODE_EXAMPLE";
        return "DOCUMENT";
    }

    private String extractTopic(String input) {
        int idx = input.indexOf("topic=");
        if (idx > 0) {
            String rest = input.substring(idx + 6);
            int end = rest.indexOf(",");
            return end > 0 ? rest.substring(0, end).trim() : rest.trim();
        }
        return input.length() > 50 ? input.substring(0, 50) : input;
    }

    private String extractDifficulty(String input) {
        if (input.contains("BEGINNER")) return "入门";
        if (input.contains("INTERMEDIATE")) return "进阶";
        if (input.contains("ADVANCED")) return "高级";
        return "进阶";
    }
}
