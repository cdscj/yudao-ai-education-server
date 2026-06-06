package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFriendDO;

import java.util.List;

/**
 * AI 好友 Service 接口
 *
 * @author 芋道源码
 */
public interface AiFriendService {

    /**
     * 发送好友申请
     *
     * @param userId       用户编号
     * @param friendUserId 好友用户编号
     * @param remark       备注
     */
    void sendFriendRequest(Long userId, Long friendUserId, String remark);

    /**
     * 接受好友申请
     *
     * @param requestId 申请编号
     * @param userId    用户编号
     */
    void acceptFriendRequest(Long requestId, Long userId);

    /**
     * 拒绝好友申请
     *
     * @param requestId 申请编号
     * @param userId    用户编号
     */
    void rejectFriendRequest(Long requestId, Long userId);

    /**
     * 删除好友
     *
     * @param userId       用户编号
     * @param friendUserId 好友用户编号
     */
    void removeFriend(Long userId, Long friendUserId);

    /**
     * 拉黑用户
     *
     * @param userId     用户编号
     * @param blockUserId 被拉黑用户编号
     */
    void blockUser(Long userId, Long blockUserId);

    /**
     * 获取好友列表
     *
     * @param userId   用户编号
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 好友列表
     */
    List<AiFriendDO> getFriendList(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 获取待处理的好友申请
     *
     * @param userId 用户编号
     * @return 待处理的申请列表
     */
    List<AiFriendDO> getPendingRequests(Long userId);

}
