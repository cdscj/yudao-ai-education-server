package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFollowDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiFollowMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.FOLLOW_SELF;

/**
 * AI 用户关注 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AiFollowServiceImpl implements AiFollowService {

    @Resource
    private AiFollowMapper followMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void follow(Long userId, Long followUserId) {
        if (userId.equals(followUserId)) {
            throw exception(FOLLOW_SELF);
        }

        AiFollowDO existing = followMapper.selectByUserIdAndFollowUserId(userId, followUserId);
        if (existing == null) {
            AiFollowDO follow = new AiFollowDO();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            followMapper.insert(follow);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfollow(Long userId, Long followUserId) {
        followMapper.deleteByUserIdAndFollowUserId(userId, followUserId);
    }

    @Override
    public List<AiFollowDO> getFollowingList(Long userId, Integer pageNo, Integer pageSize) {
        return followMapper.selectListByUserId(userId);
    }

    @Override
    public List<AiFollowDO> getFollowerList(Long userId, Integer pageNo, Integer pageSize) {
        return followMapper.selectListByFollowUserId(userId);
    }

    @Override
    public Boolean isFollowing(Long userId, Long targetUserId) {
        AiFollowDO follow = followMapper.selectByUserIdAndFollowUserId(userId, targetUserId);
        return follow != null;
    }
}
