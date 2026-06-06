package cn.iocoder.yudao.module.ai.enums.school;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 课程类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiCourseTypeEnum implements ArrayValuable<String> {

    REQUIRED("REQUIRED", "必修"),
    ELECTIVE("ELECTIVE", "选修"),
    PUBLIC("PUBLIC", "公选");

    private final String type;
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(AiCourseTypeEnum::getType).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
