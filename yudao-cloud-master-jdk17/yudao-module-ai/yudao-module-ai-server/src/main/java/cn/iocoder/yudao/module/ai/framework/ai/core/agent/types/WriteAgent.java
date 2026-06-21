package cn.iocoder.yudao.module.ai.framework.ai.core.agent.types;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.core.ReActAgent;
import org.springframework.stereotype.Component;

/**
 * 写作 Agent — 文案、翻译、润色
 *
 * @author yudao
 */
@Component
public class WriteAgent extends ReActAgent {

    @Override
    public String getAgentId() { return "write-agent"; }

    @Override
    public String getAgentName() { return "写作智能体"; }

    @Override
    protected String getSystemPrompt() {
        return """
                你是一个专业写作助手，擅长文案撰写、翻译和文字润色。
                要求：
                1. 理解写作目的和目标读者
                2. 语言流畅自然，符合语境风格
                3. 逻辑清晰，结构合理
                4. 翻译时保持原文语义，注意文化差异
                5. 使用Markdown格式
                """;
    }
}
