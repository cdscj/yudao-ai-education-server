package cn.iocoder.yudao.module.ai.enums.config;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 统计粒度枚举
 */
@Getter
@AllArgsConstructor
public enum AiStatisticsGranularityEnum implements ArrayValuable<Integer> {

    HOUR(0, "小时"),
    DAY(1, "日"),
    WEEK(2, "周"),
    MONTH(3, "月");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiStatisticsGranularityEnum::getType).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
