package cn.iocoder.yudao.module.ai.service.social;

import cn.hutool.core.date.DateUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiCheckInRecordDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiCheckInRecordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CHECK_IN_TODAY_EXISTS;

/**
 * AI 签到 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AiCheckInServiceImpl implements AiCheckInService {

    @Resource
    private AiCheckInRecordMapper checkInRecordMapper;
    @Resource
    private AiUserPointsService userPointsService;
    @Resource
    private AiUserActivityService userActivityService;

    private static final List<String> ENCOURAGEMENTS = Arrays.asList(
            "坚持就是胜利！已连续签到%d天，继续保持！",
            "今天的努力是明天的基石，签到第%d天！",
            "学习贵在坚持，你已经连续签到%d天了！",
            "每一天的签到都是对知识的渴望，已连续%d天！",
            "滴水穿石，非一日之功。已签到%d天！",
            "你的坚持让人敬佩，连续签到%d天！",
            "学习路上有你真好，已连续签到%d天！",
            "星光不负赶路人，你已经坚持了%d天！",
            "日积月累，终成江海。签到第%d天！",
            "自律的你最棒！已将签到%d天！"
    );

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiCheckInRecordDO checkIn(Long userId) {
        // 1. 查询今日是否已签到
        LocalDate today = LocalDate.now();
        AiCheckInRecordDO todayRecord = checkInRecordMapper.selectByUserIdAndDate(userId, today);
        if (todayRecord != null) {
            throw exception(CHECK_IN_TODAY_EXISTS);
        }

        // 2. 计算连续签到天数和总天数
        AiCheckInRecordDO lastRecord = checkInRecordMapper.selectLastByUserId(userId);
        int streakDays;
        if (lastRecord != null) {
            LocalDate yesterday = today.minusDays(1);
            if (lastRecord.getCheckInDate().equals(yesterday)) {
                streakDays = lastRecord.getStreakDays() + 1;
            } else {
                streakDays = 1;
            }
        } else {
            streakDays = 1;
        }
        long totalDays = checkInRecordMapper.selectCountByUserId(userId) + 1;

        // 3. 计算积分
        int basePoints = 10;
        if (streakDays >= 100) {
            basePoints += 20;
        } else if (streakDays >= 30) {
            basePoints += 10;
        } else if (streakDays >= 7) {
            basePoints += 5;
        }

        // 4. 生成 AI 鼓励语
        String encouragement = generateEncouragement(streakDays);

        // 5. 插入签到记录
        AiCheckInRecordDO record = new AiCheckInRecordDO();
        record.setUserId(userId);
        record.setCheckInDate(today);
        record.setStreakDays(streakDays);
        record.setTotalDays((int) totalDays);
        record.setPointsEarned(basePoints);
        record.setAiEncouragement(encouragement);
        checkInRecordMapper.insert(record);

        // 6. 更新用户积分
        userPointsService.addPoints(userId, basePoints, 1, record.getId());

        // 7. 发布动态
        userActivityService.publishActivity(userId, 1, encouragement, record.getId());

        return record;
    }

    @Override
    public Map<String, Object> getCheckInSummary(Long userId) {
        Map<String, Object> summary = new HashMap<>();
        long totalDays = checkInRecordMapper.selectCountByUserId(userId);
        AiCheckInRecordDO lastRecord = checkInRecordMapper.selectLastByUserId(userId);
        int streakDays = lastRecord != null ? lastRecord.getStreakDays() : 0;
        boolean todayChecked = lastRecord != null && lastRecord.getCheckInDate().equals(LocalDate.now());

        summary.put("totalDays", (int) totalDays);
        summary.put("streakDays", streakDays);
        summary.put("todayChecked", todayChecked);
        return summary;
    }

    @Override
    public List<AiCheckInRecordDO> getCheckInRecords(Long userId, Integer pageNo, Integer pageSize) {
        return checkInRecordMapper.selectListByUserId(userId, pageNo, pageSize);
    }

    @Override
    public List<AiCheckInRecordDO> getCheckInCalendar(Long userId, String yearMonth) {
        return checkInRecordMapper.selectListByUserIdAndMonth(userId, yearMonth);
    }

    private String generateEncouragement(int streakDays) {
        Random random = new Random();
        String template = ENCOURAGEMENTS.get(random.nextInt(ENCOURAGEMENTS.size()));
        return String.format(template, streakDays);
    }
}
