package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserPointsDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiUserPointsMapper;
import cn.iocoder.yudao.module.member.api.user.MemberUserApi;
import cn.iocoder.yudao.module.member.api.user.dto.MemberUserRespDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 排行榜 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AiLeaderboardServiceImpl implements AiLeaderboardService {

    private static final String REDIS_KEY_PREFIX = "ai:leaderboard:";

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private AiUserPointsMapper userPointsMapper;

    @Resource
    private MemberUserApi memberUserApi;

    @Override
    public List<Map<String, Object>> getLeaderboard(String periodType, Integer topN) {
        String periodValue = getCurrentPeriodValue(periodType);
        String redisKey = REDIS_KEY_PREFIX + periodType + ":" + periodValue;

        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<String>> tuples = zSetOps.reverseRangeWithScores(redisKey, 0, topN - 1);

        List<Map<String, Object>> leaderboard = new ArrayList<>();
        if (tuples == null || tuples.isEmpty()) {
            return leaderboard;
        }

        // 收集所有 userId，批量查询用户昵称
        List<Long> userIds = tuples.stream()
                .map(t -> Long.valueOf(t.getValue()))
                .collect(Collectors.toList());
        Map<Long, MemberUserRespDTO> userMap = getUserMap(userIds);

        int rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : tuples) {
            Long userId = Long.valueOf(tuple.getValue());
            Map<String, Object> entry = new HashMap<>();
            entry.put("rank", rank++);
            entry.put("userId", userId);
            entry.put("score", tuple.getScore() != null ? tuple.getScore().intValue() : 0);

            // 填充用户昵称
            MemberUserRespDTO user = userMap != null ? userMap.get(userId) : null;
            entry.put("nickname", user != null ? user.getNickname() : null);

            // 填充等级和称号
            AiUserPointsDO pointsDO = userPointsMapper.selectByUserId(userId);
            entry.put("level", pointsDO != null ? pointsDO.getLevel() : 1);
            entry.put("rankTitle", pointsDO != null ? pointsDO.getRankTitle() : "学习新手");

            leaderboard.add(entry);
        }
        return leaderboard;
    }

    /**
     * 批量获取用户信息，失败时降级返回 null
     */
    private Map<Long, MemberUserRespDTO> getUserMap(List<Long> userIds) {
        try {
            return memberUserApi.getUserMap(userIds);
        } catch (Exception e) {
            log.warn("[getUserMap][批量查询用户信息失败，降级处理]", e);
            return null;
        }
    }

    @Override
    public void updateScore(Long userId, Integer score, String periodType) {
        String periodValue = getCurrentPeriodValue(periodType);
        String redisKey = REDIS_KEY_PREFIX + periodType + ":" + periodValue;

        stringRedisTemplate.opsForZSet().incrementScore(redisKey, String.valueOf(userId), score);
    }

    @Override
    public Map<String, Object> getMyRank(Long userId, String periodType) {
        String periodValue = getCurrentPeriodValue(periodType);
        String redisKey = REDIS_KEY_PREFIX + periodType + ":" + periodValue;

        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        Long rank = zSetOps.reverseRank(redisKey, String.valueOf(userId));
        Double score = zSetOps.score(redisKey, String.valueOf(userId));

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("rank", rank != null ? rank + 1 : 0);
        result.put("score", score != null ? score.intValue() : 0);
        result.put("periodType", periodType);
        result.put("periodValue", periodValue);

        // 填充用户昵称
        try {
            MemberUserRespDTO user = memberUserApi.getUser(userId).getCheckedData();
            result.put("nickname", user != null ? user.getNickname() : null);
        } catch (Exception e) {
            log.warn("[getMyRank][用户({}) 获取昵称失败，降级处理]", userId, e);
            result.put("nickname", null);
        }

        return result;
    }

    @Override
    public void archiveLeaderboard() {
        // 默认实现：记录日志，实际归档逻辑需根据业务需求完善
        log.info("[archiveLeaderboard] 排行榜归档任务执行");
    }

    private String getCurrentPeriodValue(String periodType) {
        LocalDate now = LocalDate.now();
        if ("DAILY".equalsIgnoreCase(periodType)) {
            return now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else if ("WEEKLY".equalsIgnoreCase(periodType)) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault());
            int weekNumber = now.get(weekFields.weekOfYear());
            return now.getYear() + "-W" + String.format("%02d", weekNumber);
        } else if ("MONTHLY".equalsIgnoreCase(periodType)) {
            return now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        }
        // default to monthly
        return now.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }
}
