package cn.iocoder.yudao.module.ai.framework.ai.core.safety;

import cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker.HarmfulContentChecker;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker.PiiChecker;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker.PromptInjectionChecker;
import cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker.SensitiveWordChecker;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * AI 内容安全审核管道 — 顺序执行多个检查器
 *
 * <p>管道流程：
 * <pre>
 *   用户输入 → [敏感词] → [PII检测] → [Prompt注入] → [有害内容] → 审核结果
 *   任一检查器返回 BLOCK → 后续检查器跳过 → 返回 BLOCK 结果
 * </pre>
 * </p>
 *
 * @author yudao
 */
@Service
@Slf4j
public class AiContentSafetyPipeline {

    @Resource
    private SensitiveWordChecker sensitiveWordChecker;
    @Resource
    private PiiChecker piiChecker;
    @Resource
    private PromptInjectionChecker promptInjectionChecker;
    @Resource
    private HarmfulContentChecker harmfulContentChecker;

    /**
     * 审核用户输入（仅输入阶段）
     */
    public AiSafetyResult checkInput(String content) {
        AiSafetyResult result = new AiSafetyResult();

        // 1. 敏感词检查
        AiSafetyResult.Level level = sensitiveWordChecker.check(content);
        result.addResult("SensitiveWord", level,
                level == AiSafetyResult.Level.BLOCK ? "包含敏感词" : null);
        if (result.isBlocked()) return result;

        // 2. PII 检测
        PiiChecker.PiiResult piiResult = piiChecker.check(content);
        result.addResult("PII", piiResult.getLevel(), piiResult.getDetail());
        if (piiResult.getLevel() == AiSafetyResult.Level.MASK) {
            result.setMaskedContent(piiResult.getMaskedText());
        }
        if (result.isBlocked()) return result;

        // 3. Prompt 注入检测
        AiSafetyResult.Level injectionLevel = promptInjectionChecker.check(content);
        result.addResult("PromptInjection", injectionLevel,
                injectionLevel == AiSafetyResult.Level.BLOCK ? "检测到 Prompt 注入尝试" : null);
        if (result.isBlocked()) return result;

        // 4. 有害内容检测
        AiSafetyResult.Level harmfulLevel = harmfulContentChecker.check(content);
        result.addResult("HarmfulContent", harmfulLevel,
                harmfulLevel == AiSafetyResult.Level.BLOCK ? "检测到有害内容" : null);

        return result;
    }

    /**
     * 审核模型输出（仅检查有害内容）
     */
    public AiSafetyResult checkOutput(String content) {
        AiSafetyResult result = new AiSafetyResult();
        AiSafetyResult.Level harmfulLevel = harmfulContentChecker.check(content);
        result.addResult("HarmfulContent", harmfulLevel,
                harmfulLevel == AiSafetyResult.Level.BLOCK ? "输出包含有害内容" : null);
        return result;
    }
}
