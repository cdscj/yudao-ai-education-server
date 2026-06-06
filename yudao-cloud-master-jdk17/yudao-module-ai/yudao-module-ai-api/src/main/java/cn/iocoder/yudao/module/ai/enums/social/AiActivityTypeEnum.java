package cn.iocoder.yudao.module.ai.enums.social;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 用户动态类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiActivityTypeEnum implements ArrayValuable<Integer> {

    CHECK_IN(1, "签到"),
    COMPLETE_RESOURCE(2, "完成资源"),
    EARN_POINTS(3, "获得积分"),
    LEVEL_UP(4, "等级提升"),
    ADD_FRIEND(5, "添加好友"),
    COMPLETE_GOAL(6, "完成目标");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiActivityTypeEnum::getType).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
