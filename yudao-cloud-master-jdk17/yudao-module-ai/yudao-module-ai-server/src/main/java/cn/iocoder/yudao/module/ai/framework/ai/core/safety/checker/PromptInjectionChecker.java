package cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker;

import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Prompt 注入检测器
 *
 * <p>检测常见的 Prompt 注入模式，包括：
 * <ul>
 *   <li>忽略/覆盖 system prompt 指令</li>
 *   <li>角色扮演破解 (DAN/jailbreak)</li>
 *   <li>直接输出 system prompt</li>
 *   <li>越狱前缀</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class PromptInjectionChecker {

    private static final List<Pattern> INJECTION_PATTERNS = List.of(
            // 忽略指令类
            Pattern.compile("ignore\\s+(all\\s+)?(previous|above|your)\\s+(instructions?|prompts?|rules?)",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("forget\\s+(all\\s+)?(previous|above|your)\\s+(instructions?|prompts?|rules?)",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("disregard\\s+(all\\s+)?(previous|above|your)\\s+(instructions?|prompts?)",
                    Pattern.CASE_INSENSITIVE),
            // 越狱类
            Pattern.compile("(DAN|jailbreak|developer\\s*mode)",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("you\\s+are\\s+now\\s+(a\\s+)?(different|new)",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("pretend\\s+(you\\s+are|to\\s+be)",
                    Pattern.CASE_INSENSITIVE),
            // System prompt 泄露
            Pattern.compile("(repeat|output|print|show|tell\\s+me)\\s+(your|the)\\s+(system\\s+)?(prompt|instructions?|rules?)",
                    Pattern.CASE_INSENSITIVE),
            Pattern.compile("what\\s+(are|is)\\s+(your|the)\\s+(system\\s+)?(prompt|instructions?)",
                    Pattern.CASE_INSENSITIVE),
            // 分隔符注入
            Pattern.compile("(system|assistant|user)\\s*:\\s*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE)
    );

    public AiSafetyResult.Level check(String content) {
        if (content == null || content.isEmpty()) {
            return AiSafetyResult.Level.PASS;
        }
        for (Pattern pattern : INJECTION_PATTERNS) {
            if (pattern.matcher(content).find()) {
                log.warn("[PromptInjection] 检测到 Prompt 注入尝试: pattern={}", pattern.pattern());
                return AiSafetyResult.Level.BLOCK;
            }
        }
        return AiSafetyResult.Level.PASS;
    }
}
