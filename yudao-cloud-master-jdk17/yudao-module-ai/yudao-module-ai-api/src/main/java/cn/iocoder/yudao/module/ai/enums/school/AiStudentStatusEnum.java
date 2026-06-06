package cn.iocoder.yudao.module.ai.enums.school;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 学生状态枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiStudentStatusEnum implements ArrayValuable<Integer> {

    STUDYING(0, "在读"),
    GRADUATED(1, "已毕业"),
    SUSPENDED(2, "休学");

    private final Integer status;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiStudentStatusEnum::getStatus).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
