package cn.iocoder.yudao.module.ai.enums.school;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 周类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiWeekTypeEnum implements ArrayValuable<String> {

    EVERY("EVERY", "每周"),
    ODD("ODD", "单周"),
    EVEN("EVEN", "双周");

    private final String type;
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(AiWeekTypeEnum::getType).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
