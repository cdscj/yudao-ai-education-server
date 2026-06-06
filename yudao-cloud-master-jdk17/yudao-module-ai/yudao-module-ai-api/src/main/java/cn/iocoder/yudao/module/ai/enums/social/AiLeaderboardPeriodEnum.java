package cn.iocoder.yudao.module.ai.enums.social;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 排行榜周期类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiLeaderboardPeriodEnum implements ArrayValuable<String> {

    DAILY("DAILY", "日榜"),
    WEEKLY("WEEKLY", "周榜"),
    MONTHLY("MONTHLY", "月榜");

    private final String period;
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(AiLeaderboardPeriodEnum::getPeriod).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
