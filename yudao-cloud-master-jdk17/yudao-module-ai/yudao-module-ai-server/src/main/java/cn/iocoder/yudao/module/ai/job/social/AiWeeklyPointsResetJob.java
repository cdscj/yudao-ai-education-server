package cn.iocoder.yudao.module.ai.job.social;

import cn.iocoder.yudao.module.ai.dal.mysql.social.AiUserPointsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每周积分重置 Job
 *
 * 每周一凌晨 0 点执行，将所有用户的周积分（weekly_points）重置为 0。
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class AiWeeklyPointsResetJob {

    @Resource
    private AiUserPointsMapper userPointsMapper;

    @Scheduled(cron = "0 0 0 * * MON")
    public void resetWeeklyPoints() {
        log.info("[resetWeeklyPoints][开始重置每周积分]");
        try {
            int count = userPointsMapper.resetWeeklyPoints();
            log.info("[resetWeeklyPoints][每周积分重置完成，共重置 {} 条记录]", count);
        } catch (Exception e) {
            log.error("[resetWeeklyPoints][每周积分重置异常]", e);
        }
    }

}
