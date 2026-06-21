package cn.iocoder.yudao.module.ai.framework.ai.core.safety;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 内容安全审核结果
 *
 * @author yudao
 */
@Data
public class AiSafetyResult {

    /** 最终判定 */
    private Level level = Level.PASS;

    /** 各检查器的结果 */
    private final List<CheckerResult> checkerResults = new ArrayList<>();

    /** 脱敏后的文本（当 MASK 时有效） */
    private String maskedContent;

    public void addResult(String checkerName, Level level, String detail) {
        checkerResults.add(new CheckerResult(checkerName, level, detail));
        // 取最严格的级别
        if (level.getSeverity() > this.level.getSeverity()) {
            this.level = level;
        }
    }

    public boolean isBlocked() {
        return level == Level.BLOCK;
    }

    @Data
    public static class CheckerResult {
        private final String checkerName;
        private final Level level;
        private final String detail;

        public CheckerResult(String checkerName, Level level, String detail) {
            this.checkerName = checkerName;
            this.level = level;
            this.detail = detail;
        }
    }

    public enum Level {
        /** 通过 */
        PASS(0),
        /** 标记但放行 */
        WARN(1),
        /** 脱敏后放行 */
        MASK(2),
        /** 拦截 */
        BLOCK(3);

        private final int severity;
        Level(int severity) { this.severity = severity; }
        public int getSeverity() { return severity; }
    }
}
