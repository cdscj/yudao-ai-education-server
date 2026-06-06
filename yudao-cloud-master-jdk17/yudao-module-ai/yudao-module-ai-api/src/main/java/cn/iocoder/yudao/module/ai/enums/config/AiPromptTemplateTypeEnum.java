package cn.iocoder.yudao.module.ai.enums.config;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 提示词模板类型枚举
 */
@Getter
@AllArgsConstructor
public enum AiPromptTemplateTypeEnum implements ArrayValuable<Integer> {

    SYSTEM(0, "系统模板"),
    USER(1, "用户自定义");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiPromptTemplateTypeEnum::getType).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }
}
