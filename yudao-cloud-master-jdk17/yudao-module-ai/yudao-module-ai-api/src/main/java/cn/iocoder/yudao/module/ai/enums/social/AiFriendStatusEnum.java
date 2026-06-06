package cn.iocoder.yudao.module.ai.enums.social;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 好友状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiFriendStatusEnum implements ArrayValuable<Integer> {

    PENDING(0, "待确认"),
    ACCEPTED(1, "已接受"),
    BLOCKED(2, "已拉黑");

    private final Integer status;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiFriendStatusEnum::getStatus).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
