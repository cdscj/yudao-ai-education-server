package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiNotificationDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiNotificationMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.NOTIFICATION_NOT_EXISTS;

@Service
public class AiNotificationServiceImpl implements AiNotificationService {

    @Resource private AiNotificationMapper mapper;

    @Override @Transactional
    public void createNotification(Long userId, String type, String title, String content, Long refId, String refType) {
        AiNotificationDO n = new AiNotificationDO();
        n.setUserId(userId).setNotificationType(type).setTitle(title).setContent(content).setRefId(refId).setRefType(refType).setIsRead(false);
        mapper.insert(n);
    }

    @Override
    public PageResult<AiNotificationDO> getPage(Long userId, Boolean isRead, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), userId, isRead);
    }

    @Override
    public long getUnreadCount(Long userId) { return mapper.selectUnreadCount(userId); }

    @Override @Transactional
    public void markRead(Long id, Long userId) {
        AiNotificationDO n = mapper.selectById(id);
        if (n == null || !n.getUserId().equals(userId)) throw exception(NOTIFICATION_NOT_EXISTS);
        n.setIsRead(true); n.setReadTime(LocalDateTime.now()); mapper.updateById(n);
    }

    @Override @Transactional
    public void markAllRead(Long userId) {
        mapper.update(new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<AiNotificationDO>()
                .eq(AiNotificationDO::getUserId, userId).eq(AiNotificationDO::getIsRead, false)
                .set(AiNotificationDO::getIsRead, true).set(AiNotificationDO::getReadTime, LocalDateTime.now()));
    }
}
