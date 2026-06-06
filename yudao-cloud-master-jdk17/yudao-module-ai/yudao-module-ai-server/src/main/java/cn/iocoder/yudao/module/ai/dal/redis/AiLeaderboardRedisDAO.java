package cn.iocoder.yudao.module.ai.dal.redis;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

/**
 * AI 排行榜 Redis DAO
 *
 * 基于 Redis Sorted Set 实现排行榜功能，支持日榜、周榜、月榜。
 *
 * @author 芋道源码
 */
@Repository
public class AiLeaderboardRedisDAO {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "ai:leaderboard:";
    private static final DateTimeFormatter WEEK_FMT = DateTimeFormatter.ofPattern("YYYY-'W'ww");
    private static final DateTimeFormatter MONTH_FMT = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DAY_FMT = DateTimeFormatter.ISO_LOCAL_DATE;

    private String buildKey(String periodType) {
        LocalDate today = LocalDate.now();
        String period;
        switch (periodType) {
            case "WEEKLY": period = today.format(WEEK_FMT); break;
            case "MONTHLY": period = today.format(MONTH_FMT); break;
            default: period = today.format(DAY_FMT); break;
        }
        return KEY_PREFIX + periodType + ":" + period;
    }

    /**
     * 增加用户分数
     *
     * @param userId 用户编号
     * @param score 分数（增量）
     * @param periodType 周期类型：DAILY / WEEKLY / MONTHLY
     */
    public void addScore(Long userId, double score, String periodType) {
        redisTemplate.opsForZSet().incrementScore(buildKey(periodType), userId.toString(), score);
    }

    /**
     * 获取排行榜前 N 名
     *
     * @param periodType 周期类型
     * @param n 数量
     * @return 有序集合，按分数降序
     */
    public Set<ZSetOperations.TypedTuple<Object>> getTopN(String periodType, int n) {
        Set<ZSetOperations.TypedTuple<Object>> result = redisTemplate.opsForZSet()
                .reverseRangeWithScores(buildKey(periodType), 0, n - 1);
        return result != null ? result : Collections.emptySet();
    }

    /**
     * 获取用户排名（从 1 开始）
     *
     * @param userId 用户编号
     * @param periodType 周期类型
     * @return 排名，未找到返回 null
     */
    public Long getRank(Long userId, String periodType) {
        Long rank = redisTemplate.opsForZSet().reverseRank(buildKey(periodType), userId.toString());
        return rank != null ? rank + 1 : null;
    }

    /**
     * 获取用户分数
     *
     * @param userId 用户编号
     * @param periodType 周期类型
     * @return 分数，未找到返回 null
     */
    public Double getScore(Long userId, String periodType) {
        return redisTemplate.opsForZSet().score(buildKey(periodType), userId.toString());
    }

    /**
     * 获取排行榜所有成员及分数
     *
     * @param periodType 周期类型
     * @return 有序集合，按分数降序
     */
    public Set<ZSetOperations.TypedTuple<Object>> getAllWithScores(String periodType) {
        Set<ZSetOperations.TypedTuple<Object>> result = redisTemplate.opsForZSet()
                .reverseRangeWithScores(buildKey(periodType), 0, -1);
        return result != null ? result : Collections.emptySet();
    }

    /**
     * 清除当前周期的排行榜数据
     *
     * @param periodType 周期类型
     */
    public void clearPeriod(String periodType) {
        redisTemplate.delete(buildKey(periodType));
    }

}
