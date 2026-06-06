package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFollowDO;

import java.util.List;

/**
 * AI 用户关注 Service 接口
 *
 * @author 芋道源码
 */
public interface AiFollowService {

    /**
     * 关注用户
     *
     * @param userId       用户编号
     * @param followUserId 被关注用户编号
     */
    void follow(Long userId, Long followUserId);

    /**
     * 取消关注
     *
     * @param userId       用户编号
     * @param followUserId 被关注用户编号
     */
    void unfollow(Long userId, Long followUserId);

    /**
     * 获取关注列表
     *
     * @param userId   用户编号
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 关注列表
     */
    List<AiFollowDO> getFollowingList(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 获取粉丝列表
     *
     * @param userId   用户编号
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 粉丝列表
     */
    List<AiFollowDO> getFollowerList(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 是否已关注
     *
     * @param userId       用户编号
     * @param targetUserId 目标用户编号
     * @return 是否已关注
     */
    Boolean isFollowing(Long userId, Long targetUserId);

}
