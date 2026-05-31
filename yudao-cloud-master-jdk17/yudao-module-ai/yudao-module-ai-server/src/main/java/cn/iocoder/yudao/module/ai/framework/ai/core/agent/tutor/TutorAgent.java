package cn.iocoder.yudao.module.ai.framework.ai.core.agent.tutor;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyFilter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class TutorAgent implements AiAgent {

    @Resource
    private AiLlmService llmService;
    @Resource
    private AiSafetyFilter safetyFilter;

    private static final String SYSTEM_PROMPT = """
            你是高校课程辅导答疑智能体。专注计算机、AI、电子信息类专业课程。
            回答要求：
            1. 先给出简洁答案，再详细展开
            2. 引导式教学——先让学生自己思考
            3. 支持多种形式：文字详解、图解、代码示例
            4. 对于复杂问题可拆解为多个子问题
            5. 使用Markdown格式，代码用`标注
            6. 严禁给出错误/误导性信息，不确定时需说明
            """;

    @Override
    public String getAgentId() { return "tutor-agent"; }

    @Override
    public String getAgentName() { return "答疑智能体"; }

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
