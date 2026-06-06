package cn.iocoder.yudao.module.ai.enums.config;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 配额类型枚举
 */
@Getter
@AllArgsConstructor
public enum AiQuotaTypeEnum implements ArrayValuable<Integer> {

    BY_COUNT(0, "按次"),
    BY_TOKEN(1, "按Token"),
    BY_DURATION(2, "按时长");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiQuotaTypeEnum::getType).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
