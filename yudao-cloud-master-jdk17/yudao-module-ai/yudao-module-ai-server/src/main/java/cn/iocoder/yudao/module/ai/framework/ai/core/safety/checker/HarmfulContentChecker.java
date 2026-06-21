package cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker;

import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 有害内容检测器
 *
 * <p>基于关键词匹配的快速检测。生产环境可接入：
 * <ul>
 *   <li>阿里云内容安全 API</li>
 *   <li>腾讯云天御</li>
 *   <li>OpenAI Moderation API</li>
 *   <li>本地有害内容分类模型</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class HarmfulContentChecker {

    // 高风险关键词（简化版，生产应接入专业审核 API）
    private static final List<String> HIGH_RISK_CN = List.of(
            "制作炸弹", "如何制毒", "贩卖枪支", "黑客入侵",
            "人肉搜索", "网络暴力", "自杀方法"
    );

    private static final List<String> HIGH_RISK_EN = List.of(
            "how to make a bomb", "how to synthesize drugs",
            "child abuse", "human trafficking"
    );

    public AiSafetyResult.Level check(String content) {
        if (content == null || content.isEmpty()) {
            return AiSafetyResult.Level.PASS;
        }
        String lowerContent = content.toLowerCase();

        for (String keyword : HIGH_RISK_CN) {
            if (lowerContent.contains(keyword.toLowerCase())) {
                log.warn("[HarmfulContent] 检测到高风险内容: {}", keyword);
                return AiSafetyResult.Level.BLOCK;
            }
        }
        for (String keyword : HIGH_RISK_EN) {
            if (lowerContent.contains(keyword.toLowerCase())) {
                log.warn("[HarmfulContent] 检测到高风险内容(EN): {}", keyword);
                return AiSafetyResult.Level.BLOCK;
            }
        }
        return AiSafetyResult.Level.PASS;
    }
}
