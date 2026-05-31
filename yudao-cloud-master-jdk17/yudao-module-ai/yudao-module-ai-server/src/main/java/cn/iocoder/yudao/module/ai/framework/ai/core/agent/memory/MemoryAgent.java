package cn.iocoder.yudao.module.ai.framework.ai.core.agent.memory;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.framework.ai.core.agent.AiAgent;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@Slf4j
public class MemoryAgent implements AiAgent {

    @Resource
    private AiLlmService llmService;

    private static final String COMPRESS_PROMPT = """
            你是一个上下文记忆压缩智能体。将以下对话历史压缩为简洁的摘要，保留关键信息：
            1. 用户的学习目标、专业、年级
            2. 已讨论的知识点
            3. 用户的薄弱环节
            4. 当前学习进度
            输出不超过200字的摘要。
            """;

    @Override
    public String getAgentId() { return "memory-agent"; }

    @Override
    public String getAgentName() { return "记忆压缩智能体"; }

    @Override
    public Flux<String> execute(String input, String context) {
        return compress(input);
    }

    @Override
    public Flux<String> executeStream(String input, String context) {
        return compress(input);
    }

    public Flux<String> compress(String history) {
        if (StrUtil.length(history) < 500) {
            return Flux.just(history);
        }
        return llmService.chatStream(COMPRESS_PROMPT, history, "");
    }

    public String compressSync(String history) {
        if (StrUtil.length(history) < 500) {
            return history;
        }
        return llmService.chatSync(COMPRESS_PROMPT, history, "");
    }
}
