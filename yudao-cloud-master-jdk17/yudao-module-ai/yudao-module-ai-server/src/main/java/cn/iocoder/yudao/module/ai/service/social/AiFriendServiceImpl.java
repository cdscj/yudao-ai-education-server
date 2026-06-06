package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFriendDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiFriendMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI 好友 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class AiFriendServiceImpl implements AiFriendService {

    @Resource
    private AiFriendMapper friendMapper;

    private static final Integer STATUS_PENDING = 0;
    private static final Integer STATUS_ACCEPTED = 1;
    private static final Integer STATUS_REJECTED = 2;
    private static final Integer STATUS_BLOCKED = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendFriendRequest(Long userId, Long friendUserId, String remark) {
        // 1. 校验不能添加自己
        if (userId.equals(friendUserId)) {
            throw exception(FRIEND_SELF);
        }

        // 2. 检查是否已存在申请
        AiFriendDO existing = friendMapper.selectByUserIdAndFriendUserId(userId, friendUserId);
        if (existing != null) {
            throw exception(FRIEND_REQUEST_EXISTS);
        }

        // 3. 检查是否已是好友
        AiFriendDO reverse = friendMapper.selectByUserIdAndFriendUserId(friendUserId, userId);
        if (reverse != null && STATUS_ACCEPTED.equals(reverse.getStatus())) {
            throw exception(FRIEND_ALREADY_ACCEPTED);
        }

        // 4. 检查是否被拉黑
        if (reverse != null && STATUS_BLOCKED.equals(reverse.getStatus())) {
            throw exception(FRIEND_BLOCKED);
        }

        // 5. 插入申请记录
        AiFriendDO friend = new AiFriendDO();
        friend.setUserId(userId);
        friend.setFriendUserId(friendUserId);
        friend.setStatus(STATUS_PENDING);
        friend.setRemark(remark);
        friendMapper.insert(friend);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void acceptFriendRequest(Long requestId, Long userId) {
        AiFriendDO request = friendMapper.selectById(requestId);
        if (request == null) {
            throw exception(FRIEND_NOT_EXISTS);
        }

        // 校验是发给自己的
        if (!request.getFriendUserId().equals(userId)) {
            throw exception(FRIEND_NOT_EXISTS);
        }

        // 更新状态为已接受
        request.setStatus(STATUS_ACCEPTED);
        friendMapper.updateById(request);

        // 插入反向记录（双向好友）
        AiFriendDO reverse = friendMapper.selectByUserIdAndFriendUserId(request.getFriendUserId(), request.getUserId());
        if (reverse == null) {
            reverse = new AiFriendDO();
            reverse.setUserId(request.getFriendUserId());
            reverse.setFriendUserId(request.getUserId());
            reverse.setStatus(STATUS_ACCEPTED);
            reverse.setRemark(request.getRemark());
            friendMapper.insert(reverse);
        } else {
            reverse.setStatus(STATUS_ACCEPTED);
            friendMapper.updateById(reverse);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rejectFriendRequest(Long requestId, Long userId) {
        AiFriendDO request = friendMapper.selectById(requestId);
        if (request == null) {
            throw exception(FRIEND_NOT_EXISTS);
        }

        // 校验是发给自己的
        if (!request.getFriendUserId().equals(userId)) {
            throw exception(FRIEND_NOT_EXISTS);
        }

        request.setStatus(STATUS_REJECTED);
        friendMapper.updateById(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeFriend(Long userId, Long friendUserId) {
        friendMapper.deleteByUserIdAndFriendUserId(userId, friendUserId);
        friendMapper.deleteByUserIdAndFriendUserId(friendUserId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void blockUser(Long userId, Long blockUserId) {
        // 删除现有的好友关系
        friendMapper.deleteByUserIdAndFriendUserId(userId, blockUserId);
        friendMapper.deleteByUserIdAndFriendUserId(blockUserId, userId);

        // 添加拉黑记录
        AiFriendDO blockRecord = new AiFriendDO();
        blockRecord.setUserId(userId);
        blockRecord.setFriendUserId(blockUserId);
        blockRecord.setStatus(STATUS_BLOCKED);
        friendMapper.insert(blockRecord);
    }

    @Override
    public List<AiFriendDO> getFriendList(Long userId, Integer pageNo, Integer pageSize) {
        return friendMapper.selectListByUserIdAndStatus(userId, STATUS_ACCEPTED);
    }

    @Override
    public List<AiFriendDO> getPendingRequests(Long userId) {
        return friendMapper.selectListByFriendUserIdAndStatus(userId, STATUS_PENDING);
    }
}
