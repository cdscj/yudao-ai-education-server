package cn.iocoder.yudao.module.ai.dal.dataobject.social;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * AI 排行榜归档 DO
 *
 * @author 芋道源码
 */
@TableName(value = "ai_leaderboard")
@KeySequence("ai_leaderboard_seq")
@Data
public class AiLeaderboardDO extends BaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 周期类型
     */
    private String periodType;
    /**
     * 周期值
     */
    private String periodValue;
    /**
     * 积分
     */
    private Integer score;
    /**
     * 排名
     */
    private Integer rank;

}
