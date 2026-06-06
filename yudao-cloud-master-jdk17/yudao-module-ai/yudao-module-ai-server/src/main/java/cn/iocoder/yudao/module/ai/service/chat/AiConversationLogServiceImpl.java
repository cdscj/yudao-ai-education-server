package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.log.AiConversationLogPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiConversationLogDO;
import cn.iocoder.yudao.module.ai.dal.mysql.chat.AiConversationLogMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static cn.iocoder.yudao.module.ai.framework.config.AiAsyncConfiguration.AI_THREAD_POOL_TASK_EXECUTOR;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.CONVERSATION_LOG_NOT_EXISTS;

/**
 * AI 对话日志 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class AiConversationLogServiceImpl implements AiConversationLogService {

    @Resource
    private AiConversationLogMapper conversationLogMapper;

    @Override
    @Async(AI_THREAD_POOL_TASK_EXECUTOR)
    public void createConversationLog(AiConversationLogDO conversationLog) {
        try {
            conversationLogMapper.insert(conversationLog);
        } catch (Exception e) {
            log.error("[createConversationLog][记录对话日志异常，log({})]", conversationLog, e);
        }
    }

    @Override
    public PageResult<AiConversationLogDO> getConversationLogPage(AiConversationLogPageReqVO pageReqVO) {
        return conversationLogMapper.selectPage(pageReqVO);
    }

    @Override
    public AiConversationLogDO getConversationLog(Long id) {
        return conversationLogMapper.selectById(id);
    }

    @Override
    public Long getConversationLogCount(String startDate, String endDate) {
        LocalDateTime start = startDate != null ? LocalDate.parse(startDate).atStartOfDay() : LocalDate.of(1970, 1, 1).atStartOfDay();
        LocalDateTime end = endDate != null ? LocalDate.parse(endDate).plusDays(1).atStartOfDay() : LocalDateTime.now().plusDays(1);
        return conversationLogMapper.selectCountByDateRange(start, end);
    }

    @Override
    public void deleteConversationLog(Long id) {
        // 校验存在
        AiConversationLogDO log = conversationLogMapper.selectById(id);
        if (log == null) {
            throw exception(CONVERSATION_LOG_NOT_EXISTS);
        }
        // 删除
        conversationLogMapper.deleteById(id);
    }

}
