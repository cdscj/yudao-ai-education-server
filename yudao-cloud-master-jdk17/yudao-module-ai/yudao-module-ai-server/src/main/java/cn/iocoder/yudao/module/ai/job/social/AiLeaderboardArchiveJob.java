package cn.iocoder.yudao.module.ai.job.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLeaderboardRecordDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiLeaderboardRecordMapper;
import cn.iocoder.yudao.module.ai.dal.redis.AiLeaderboardRedisDAO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;

/**
 * 排行榜数据归档 Job
 *
 * 每天凌晨 1 点执行，将 Redis 中的排行榜数据归档到 MySQL，并根据周期清理 Redis 缓存。
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class AiLeaderboardArchiveJob {

    @Resource
    private AiLeaderboardRedisDAO leaderboardRedisDAO;

    @Resource
    private AiLeaderboardRecordMapper leaderboardRecordMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    public void archiveLeaderboard() {
        log.info("[archiveLeaderboard][开始执行排行榜数据归档]");
        LocalDate today = LocalDate.now();
        String[] periodTypes = {"WEEKLY", "MONTHLY"};

        for (String periodType : periodTypes) {
            try {
                Set<ZSetOperations.TypedTuple<Object>> scores = leaderboardRedisDAO.getAllWithScores(periodType);
                if (scores == null || scores.isEmpty()) {
                    log.info("[archiveLeaderboard][周期 {} 暂无数据，跳过归档]", periodType);
                    continue;
                }

                int rank = 1;
                for (ZSetOperations.TypedTuple<Object> tuple : scores) {
                    if (tuple.getValue() == null) {
                        rank++;
                        continue;
                    }
                    Long userId = Long.valueOf(tuple.getValue().toString());
                    Integer score = tuple.getScore() != null ? tuple.getScore().intValue() : 0;

                    // 查询是否已存在该周期该用户的记录
                    AiLeaderboardRecordDO existing = leaderboardRecordMapper.selectOne(
                            AiLeaderboardRecordDO::getUserId, userId,
                            AiLeaderboardRecordDO::getPeriodType, periodType,
                            AiLeaderboardRecordDO::getSnapshotDate, today);

                    if (existing != null) {
                        existing.setScore(score);
                        existing.setRank(rank);
                        leaderboardRecordMapper.updateById(existing);
                    } else {
                        AiLeaderboardRecordDO record = new AiLeaderboardRecordDO();
                        record.setUserId(userId);
                        record.setPeriodType(periodType);
                        record.setScore(score);
                        record.setRank(rank);
                        record.setSnapshotDate(today);
                        leaderboardRecordMapper.insert(record);
                    }
                    rank++;
                }
                log.info("[archiveLeaderboard][周期 {} 归档完成，共 {} 条]", periodType, scores.size());

                // 清理 Redis 缓存
                boolean shouldClean = false;
                if ("WEEKLY".equals(periodType) && today.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    shouldClean = true;
                } else if ("MONTHLY".equals(periodType) && today.getDayOfMonth() == today.lengthOfMonth()) {
                    shouldClean = true;
                }
                if (shouldClean) {
                    leaderboardRedisDAO.clearPeriod(periodType);
                    log.info("[archiveLeaderboard][已清除 Redis 周期 {} 数据]", periodType);
                }
            } catch (Exception e) {
                log.error("[archiveLeaderboard][周期 {} 归档异常]", periodType, e);
            }
        }
        log.info("[archiveLeaderboard][排行榜数据归档执行完成]");
    }

}
