package cn.iocoder.yudao.module.ai.enums.social;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 学习目标类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiGoalTypeEnum implements ArrayValuable<String> {

    STUDY_MINUTES("STUDY_MINUTES", "学习时长"),
    RESOURCE_COUNT("RESOURCE_COUNT", "资源数量"),
    CHECK_IN_STREAK("CHECK_IN_STREAK", "连续签到"),
    POINTS_TARGET("POINTS_TARGET", "积分目标");

    private final String type;
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(AiGoalTypeEnum::getType).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
