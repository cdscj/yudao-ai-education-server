package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserPointsDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiUserPointsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 用户积分 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AiUserPointsServiceImpl implements AiUserPointsService {

    @Resource
    private AiUserPointsMapper userPointsMapper;
    @Resource
    private AiLeaderboardService leaderboardService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiUserPointsDO addPoints(Long userId, Integer points, Integer bizType, Long bizId) {
        // 1. 获取或创建用户积分记录
        AiUserPointsDO userPoints = userPointsMapper.selectByUserId(userId);
        if (userPoints == null) {
            userPoints = new AiUserPointsDO();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(0);
            userPoints.setWeeklyPoints(0);
            userPoints.setMonthlyPoints(0);
            userPoints.setLevel(1);
            userPoints.setRankTitle("学习新手");
            userPointsMapper.insert(userPoints);
        }

        // 2. 更新积分
        userPoints.setTotalPoints(userPoints.getTotalPoints() + points);
        userPoints.setWeeklyPoints(userPoints.getWeeklyPoints() + points);
        userPoints.setMonthlyPoints(userPoints.getMonthlyPoints() + points);

        // 3. 计算等级
        int level = calculateLevel(userPoints.getTotalPoints());
        userPoints.setLevel(level);
        userPoints.setRankTitle(getRankTitle(level));

        // 4. 更新记录
        userPointsMapper.updateById(userPoints);

        // 5. 更新排行榜
        leaderboardService.updateScore(userId, points, "DAILY");
        leaderboardService.updateScore(userId, points, "WEEKLY");
        leaderboardService.updateScore(userId, points, "MONTHLY");

        return userPoints;
    }

    @Override
    public AiUserPointsDO getUserPoints(Long userId) {
        AiUserPointsDO userPoints = userPointsMapper.selectByUserId(userId);
        if (userPoints == null) {
            userPoints = new AiUserPointsDO();
            userPoints.setUserId(userId);
            userPoints.setTotalPoints(0);
            userPoints.setWeeklyPoints(0);
            userPoints.setMonthlyPoints(0);
            userPoints.setLevel(1);
            userPoints.setRankTitle("学习新手");
            userPointsMapper.insert(userPoints);
        }
        return userPoints;
    }

    @Override
    public List<AiUserPointsDO> getPointsHistory(Long userId, Integer pageNo, Integer pageSize) {
        return userPointsMapper.selectListByUserId(userId, pageNo, pageSize);
    }

    @Override
    public Map<String, Object> getUserLevel(Long userId) {
        AiUserPointsDO userPoints = getUserPoints(userId);
        Map<String, Object> levelInfo = new HashMap<>();
        levelInfo.put("level", userPoints.getLevel());
        levelInfo.put("rankTitle", userPoints.getRankTitle());
        levelInfo.put("totalPoints", userPoints.getTotalPoints());

        // 下一级所需积分
        int nextLevelPoints = getNextLevelPoints(userPoints.getLevel());
        levelInfo.put("nextLevelPoints", nextLevelPoints);
        levelInfo.put("remainingPoints", nextLevelPoints - userPoints.getTotalPoints());

        return levelInfo;
    }

    private int calculateLevel(int totalPoints) {
        if (totalPoints >= 5000) return 7;
        if (totalPoints >= 2000) return 6;
        if (totalPoints >= 1000) return 5;
        if (totalPoints >= 600) return 4;
        if (totalPoints >= 300) return 3;
        if (totalPoints >= 100) return 2;
        return 1;
    }

    private String getRankTitle(int level) {
        return switch (level) {
            case 1 -> "学习新手";
            case 2 -> "初级学者";
            case 3 -> "进阶学习者";
            case 4 -> "知识达人";
            case 5 -> "学习大师";
            case 6 -> "知识专家";
            case 7 -> "终身学习者";
            default -> "学习新手";
        };
    }

    private int getNextLevelPoints(int currentLevel) {
        return switch (currentLevel) {
            case 1 -> 100;
            case 2 -> 300;
            case 3 -> 600;
            case 4 -> 1000;
            case 5 -> 2000;
            case 6 -> 5000;
            case 7 -> 5000; // 最高级
            default -> 100;
        };
    }
}
