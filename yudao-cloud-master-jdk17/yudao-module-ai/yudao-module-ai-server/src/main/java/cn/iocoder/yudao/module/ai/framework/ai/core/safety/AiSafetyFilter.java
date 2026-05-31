package cn.iocoder.yudao.module.ai.framework.ai.core.safety;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class AiSafetyFilter {

    private static final List<String> SENSITIVE_KEYWORDS = List.of(
            "政治", "色情", "赌博", "暴力", "恐怖", "毒品", "反动",
            "敏感", "违法", "欺诈", "代考", "代写", "作弊"
    );

    private static final List<String> HALLUCINATION_MARKERS = List.of(
            "我不确定", "可能不正确", "请核实", "我没有相关信息",
            "建议查阅", "以实际为准", "请参考官方文档"
    );

    public String filter(String input) {
        if (StrUtil.isBlank(input)) return input;
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (input.contains(keyword)) {
                log.warn("[safety] 检测到敏感词: {}", keyword);
                return "【内容包含敏感信息，已过滤】";
            }
        }
        return input;
    }

    public String filterOutput(String output) {
        if (StrUtil.isBlank(output)) return output;
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (output.contains(keyword)) {
                log.warn("[safety] 输出含敏感词: {}", keyword);
                return "【输出内容已过滤】";
            }
        }
        return output;
    }

    public boolean isSafe(String content) {
        for (String keyword : SENSITIVE_KEYWORDS) {
            if (content.contains(keyword)) return false;
        }
        return true;
    }

    public String addAntiHallucinationSuffix(String content) {
        if (StrUtil.isBlank(content)) return content;
        if (HALLUCINATION_MARKERS.stream().anyMatch(content::contains)) {
            return content;
        }
        return content + "\n\n---\n*以上内容由AI生成，仅供参考，请结合教材和课堂内容进行学习。如有疑问，建议向老师请教。*";
    }
}
