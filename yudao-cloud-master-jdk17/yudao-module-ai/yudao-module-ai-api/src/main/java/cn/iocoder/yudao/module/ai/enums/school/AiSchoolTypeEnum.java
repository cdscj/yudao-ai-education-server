package cn.iocoder.yudao.module.ai.enums.school;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 学校类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiSchoolTypeEnum implements ArrayValuable<String> {

    UNIVERSITY("UNIVERSITY", "大学"),
    COLLEGE("COLLEGE", "学院"),
    HIGH_SCHOOL("HIGH_SCHOOL", "高中"),
    MIDDLE_SCHOOL("MIDDLE_SCHOOL", "初中"),
    PRIMARY("PRIMARY", "小学"),
    OTHER("OTHER", "其他");

    private final String type;
    private final String name;

    public static final String[] ARRAYS = Arrays.stream(values()).map(AiSchoolTypeEnum::getType).toArray(String[]::new);

    @Override
    public String[] array() {
        return ARRAYS;
    }

}
