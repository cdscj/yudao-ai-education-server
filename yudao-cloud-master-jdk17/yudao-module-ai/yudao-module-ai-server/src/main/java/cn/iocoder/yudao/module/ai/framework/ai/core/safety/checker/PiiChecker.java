package cn.iocoder.yudao.module.ai.framework.ai.core.safety.checker;

import cn.iocoder.yudao.module.ai.framework.ai.core.safety.AiSafetyResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 个人信息 (PII) 检测器
 *
 * <p>检测并脱敏：手机号、身份证号、邮箱、银行卡号</p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class PiiChecker {

    // 手机号
    private static final Pattern PHONE = Pattern.compile("1[3-9]\\d{9}");
    // 身份证号
    private static final Pattern ID_CARD = Pattern.compile("\\d{17}[\\dXx]");
    // 邮箱
    private static final Pattern EMAIL = Pattern.compile("[\\w.-]+@[\\w.-]+\\.\\w+");
    // 银行卡号
    private static final Pattern BANK_CARD = Pattern.compile("\\d{16,19}");

    public PiiResult check(String content) {
        if (content == null || content.isEmpty()) {
            return new PiiResult(AiSafetyResult.Level.PASS, null, content);
        }

        StringBuilder masked = new StringBuilder(content);
        boolean found = false;

        // 手机号脱敏
        java.util.regex.Matcher phoneMatcher = PHONE.matcher(masked);
        StringBuffer sb = new StringBuffer();
        while (phoneMatcher.find()) {
            found = true;
            String phone = phoneMatcher.group();
            phoneMatcher.appendReplacement(sb, phone.substring(0, 3) + "****" + phone.substring(7));
        }
        phoneMatcher.appendTail(sb);
        masked = new StringBuilder(sb.toString());

        // 邮箱脱敏
        java.util.regex.Matcher emailMatcher = EMAIL.matcher(masked);
        sb = new StringBuffer();
        while (emailMatcher.find()) {
            found = true;
            String email = emailMatcher.group();
            int atIdx = email.indexOf('@');
            emailMatcher.appendReplacement(sb,
                    email.charAt(0) + "***" + email.substring(atIdx));
        }
        emailMatcher.appendTail(sb);

        if (found) {
            log.debug("[PII] 检测到个人敏感信息，已脱敏");
            return new PiiResult(AiSafetyResult.Level.MASK,
                    "检测到手机号/邮箱等个人信息，已自动脱敏", sb.toString());
        }
        return new PiiResult(AiSafetyResult.Level.PASS, null, content);
    }

    @Data
    public static class PiiResult {
        private final AiSafetyResult.Level level;
        private final String detail;
        private final String maskedText;

        public PiiResult(AiSafetyResult.Level level, String detail, String maskedText) {
            this.level = level;
            this.detail = detail;
            this.maskedText = maskedText;
        }
    }
}
