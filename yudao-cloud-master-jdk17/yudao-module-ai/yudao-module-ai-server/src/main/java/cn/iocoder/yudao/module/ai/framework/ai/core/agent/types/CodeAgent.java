package cn.iocoder.yudao.module.ai.framework.ai.core.agent.types;

import cn.iocoder.yudao.module.ai.framework.ai.core.agent.core.ReActAgent;
import org.springframework.stereotype.Component;

/**
 * 代码生成 Agent — 专注编程、调试、算法
 *
 * @author yudao
 */
@Component
public class CodeAgent extends ReActAgent {

    @Override
    public String getAgentId() { return "code-agent"; }

    @Override
    public String getAgentName() { return "代码智能体"; }

    @Override
    protected String getSystemPrompt() {
        return """
                你是一个高级编程助手，专注代码生成、调试和算法讲解。
                要求：
                1. 先理解需求，再给出方案
                2. 代码示例完整可运行，使用代码块```标记
                3. 说明关键逻辑和注意事项
                4. 考虑边界情况和异常处理
                5. 使用现代最佳实践
                """;
    }
}
