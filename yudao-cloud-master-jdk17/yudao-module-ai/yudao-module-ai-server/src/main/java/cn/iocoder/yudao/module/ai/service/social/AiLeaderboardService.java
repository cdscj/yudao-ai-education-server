package cn.iocoder.yudao.module.ai.service.social;

import java.util.List;
import java.util.Map;

/**
 * AI 排行榜 Service 接口
 *
 * @author 芋道源码
 */
public interface AiLeaderboardService {

    /**
     * 获取排行榜
     *
     * @param periodType 周期类型（WEEKLY、MONTHLY）
     * @param topN       前 N 名
     * @return 排行榜列表
     */
    List<Map<String, Object>> getLeaderboard(String periodType, Integer topN);

    /**
     * 更新积分（Redis ZSet）
     *
     * @param userId     用户编号
     * @param score      积分值
     * @param periodType 周期类型
     */
    void updateScore(Long userId, Integer score, String periodType);

    /**
     * 获取我的排名
     *
     * @param userId     用户编号
     * @param periodType 周期类型
     * @return 排名信息
     */
    Map<String, Object> getMyRank(Long userId, String periodType);

    /**
     * 归档排行榜（Redis -> MySQL）
     */
    void archiveLeaderboard();

}
