package cn.iocoder.yudao.module.ai.framework.ai.core.agent;

import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyFilter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class ProfileAgent implements AiAgent {

    @Resource
    private AiLlmService llmService;
    @Resource
    private AiSafetyFilter safetyFilter;

    private static final String SYSTEM_PROMPT = """
            你是学生画像构建智能体。负责通过对话提取学生6维画像信息：
            1. 基本信息（专业、年级）
            2. 学习目标
            3. 知识水平
            4. 学习偏好（视听/阅读/实践）
            5. 薄弱环节
            6. 优势能力
            
            规则：信息不足时继续提问，足够时输出JSON。
            """;

    @Override
    public String getAgentId() { return "profile-agent"; }

    @Override
    public String getAgentName() { return "画像智能体"; }

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
