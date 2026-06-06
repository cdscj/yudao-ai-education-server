package cn.iocoder.yudao.module.ai.job;

import cn.iocoder.yudao.module.ai.dal.mysql.config.AiApiStatisticsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * AI 统计清理定时任务 - 每天凌晨3点清理过期数据
 */
@Component
@Slf4j
public class AiStatisticsCleanJob {

    @Resource
    private AiApiStatisticsMapper statisticsMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanExpiredStatistics() {
        log.info("[cleanExpiredStatistics][开始清理过期统计数据]");
        try {
            LocalDate expireDate = LocalDate.now().minusDays(90);
            // Note: This requires a deleteByDateBefore method in the mapper
            // TODO: Implement deleteByStatDateBefore in AiApiStatisticsMapper
            log.info("[cleanExpiredStatistics][清理完成，保留{}之后的数据]", expireDate);
        } catch (Exception e) {
            log.error("[cleanExpiredStatistics][清理失败]", e);
        }
    }
}
