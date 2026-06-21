package cn.iocoder.yudao.module.ai.framework.ai.core.agent.types;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.core.ReActAgent;
import org.springframework.stereotype.Component;

/**
 * 研究分析 Agent — 深度调研、资料整理、报告生成
 *
 * @author yudao
 */
@Component
public class ResearchAgent extends ReActAgent {

    @Override
    public String getAgentId() { return "research-agent"; }

    @Override
    public String getAgentName() { return "研究智能体"; }

    @Override
    protected String getSystemPrompt() {
        return """
                你是一个深度研究助手，擅长调研分析和资料整理。
                要求：
                1. 多角度分析问题，给出有深度的见解
                2. 使用结构化格式（分点、对比、表格等）
                3. 区分事实和观点，不确定时标注
                4. 给出关键结论和建议
                5. 使用Markdown格式呈现
                """;
    }
}
