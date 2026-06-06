package cn.iocoder.yudao.module.ai.job.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserActivityDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiUserActivityMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 用户活动记录清理 Job
 *
 * 每天凌晨 3 点执行，清理 90 天前的用户活动记录（物理删除）。
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class AiActivityCleanJob {

    @Resource
    private AiUserActivityMapper userActivityMapper;

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanActivity() {
        log.info("[cleanActivity][开始清理过期活动记录]");
        try {
            LocalDateTime deadline = LocalDateTime.now().minusDays(90);
            int deletedCount = userActivityMapper.delete(new LambdaQueryWrapper<AiUserActivityDO>()
                    .lt(AiUserActivityDO::getCreateTime, deadline));
            log.info("[cleanActivity][清理完成，共删除 {} 条过期活动记录]", deletedCount);
        } catch (Exception e) {
            log.error("[cleanActivity][清理活动记录异常]", e);
        }
    }

}
