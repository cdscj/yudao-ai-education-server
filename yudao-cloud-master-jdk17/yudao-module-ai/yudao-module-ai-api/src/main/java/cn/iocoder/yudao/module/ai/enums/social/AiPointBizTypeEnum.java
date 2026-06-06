package cn.iocoder.yudao.module.ai.enums.social;

import cn.iocoder.yudao.framework.common.core.ArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI 积分业务类型枚举
 *
 * @author 芋道源码
 */
@Getter
@AllArgsConstructor
public enum AiPointBizTypeEnum implements ArrayValuable<Integer> {

    CHECK_IN(1, "签到"),
    COMPLETE_RESOURCE(2, "完成资源"),
    STREAK_BONUS(3, "连续签到奖励"),
    INVITE_FRIEND(4, "邀请好友"),
    AI_REWARD(5, "AI 奖励"),
    ADMIN_GIFT(6, "管理员赠送"),
    GOAL_COMPLETE(7, "目标完成");

    private final Integer type;
    private final String name;

    public static final Integer[] ARRAYS = Arrays.stream(values()).map(AiPointBizTypeEnum::getType).toArray(Integer[]::new);

    @Override
    public Integer[] array() {
        return ARRAYS;
    }

}
