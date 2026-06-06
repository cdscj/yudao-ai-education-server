package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiNotificationDO;

public interface AiNotificationService {
    void createNotification(Long userId, String type, String title, String content, Long refId, String refType);
    PageResult<AiNotificationDO> getPage(Long userId, Boolean isRead, Integer pageNo, Integer pageSize);
    long getUnreadCount(Long userId);
    void markRead(Long id, Long userId);
    void markAllRead(Long userId);
}
