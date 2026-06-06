package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFollowDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFriendDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserActivityDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiFollowMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiFriendMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiUserActivityMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.module.ai.framework.config.AiAsyncConfiguration.AI_THREAD_POOL_TASK_EXECUTOR;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 用户动态 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AiUserActivityServiceImpl implements AiUserActivityService {

    @Resource
    private AiUserActivityMapper userActivityMapper;
    @Resource
    private AiFollowMapper followMapper;
    @Resource
    private AiFriendMapper friendMapper;

    @Override
    @Async(AI_THREAD_POOL_TASK_EXECUTOR)
    public void publishActivity(Long userId, Integer activityType, String content, Long refId) {
        AiUserActivityDO activity = new AiUserActivityDO();
        activity.setUserId(userId);
        activity.setActivityType(activityType);
        activity.setContent(content);
        activity.setRefId(refId);
        userActivityMapper.insert(activity);
        log.info("[publishActivity][userId({}) 发布动态：{}]", userId, content);
    }

    @Override
    public PageResult<AiUserActivityDO> getActivityFeed(Long userId, Integer pageNo, Integer pageSize) {
        // 获取好友和关注的人的用户ID列表
        List<Long> userIds = new ArrayList<>();
        userIds.add(userId); // 自己的动态

        // 好友的userId
        List<AiFriendDO> friends = friendMapper.selectListByUserIdAndStatus(userId, 1);
        for (AiFriendDO friend : friends) {
            userIds.add(friend.getFriendUserId());
        }

        // 关注的人的userId
        List<AiFollowDO> following = followMapper.selectListByUserId(userId);
        for (AiFollowDO follow : following) {
            userIds.add(follow.getFollowUserId());
        }

        userIds = userIds.stream().distinct().collect(Collectors.toList());
        List<AiUserActivityDO> list = userActivityMapper.selectListByUserIds(userIds, pageSize);
        return new PageResult<>(list, (long) list.size());
    }

    @Override
    public List<AiUserActivityDO> getUserActivities(Long userId, Integer pageNo, Integer pageSize) {
        return userActivityMapper.selectListByUserId(userId, pageSize);
    }
}
