package cn.iocoder.yudao.module.ai.enums.config;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 敏感词等级枚举
 */
@Getter
@AllArgsConstructor
public enum AiSensitiveWordLevelEnum implements ArrayValuable<Integer> {

    NORMAL(0, "一般"),
    MEDIUM(1, "中等"),
    SEVERE(2, "严重");

    private final Integer level;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiSensitiveWordLevelEnum::getLevel).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
