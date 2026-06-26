package cn.iocoder.yudao.module.ai.service.agent;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentMessageDO;
import cn.iocoder.yudao.module.ai.dal.mysql.agent.AiAgentMessageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.AGENT_MESSAGE_NOT_EXISTS;

/**
 * AI Agent 消息 Service 实现类
 *
 * @author yudao
 */
@Service
@Validated
public class AiAgentMessageServiceImpl implements AiAgentMessageService {

    /** 上下文最大拼接 Token 数（约 8000 字符） */
    private static final int MAX_CONTEXT_LENGTH = 16000;

    @Resource
    private AiAgentMessageMapper messageMapper;

    @Override
    public AiAgentMessageDO createUserMessage(Long conversationId, String content) {
        AiAgentMessageDO message = AiAgentMessageDO.builder()
                .conversationId(conversationId)
                .role("user")
                .content(content)
                .isError(false)
                .usageTokens(0)
                .build();
        messageMapper.insert(message);
        return message;
    }

    @Override
    public AiAgentMessageDO createAssistantMessage(Long conversationId, String content,
                                                    String reasoning, Integer usageTokens) {
        AiAgentMessageDO message = AiAgentMessageDO.builder()
                .conversationId(conversationId)
                .role("assistant")
                .content(content)
                .isError(false)
                .reasoning(reasoning)
                .usageTokens(usageTokens != null ? usageTokens : 0)
                .build();
        messageMapper.insert(message);
        return message;
    }

    @Override
    public AiAgentMessageDO createErrorMessage(Long conversationId, String errorMsg) {
        AiAgentMessageDO message = AiAgentMessageDO.builder()
                .conversationId(conversationId)
                .role("assistant")
                .content(errorMsg)
                .isError(true)
                .usageTokens(0)
                .build();
        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<AiAgentMessageDO> getMessageList(Long conversationId) {
        return messageMapper.selectListByConversationId(conversationId);
    }

    @Override
    public String buildContext(Long conversationId) {
        List<AiAgentMessageDO> messages = getMessageList(conversationId);
        StringBuilder sb = new StringBuilder();
        int totalLength = 0;

        // 从后往前取，控制总长度
        for (int i = messages.size() - 1; i >= 0; i--) {
            AiAgentMessageDO msg = messages.get(i);
            String line = ("user".equals(msg.getRole()) ? "用户：" : "助手：")
                    + StrUtil.nullToEmpty(msg.getContent()) + "\n";
            if (totalLength + line.length() > MAX_CONTEXT_LENGTH) {
                break;
            }
            sb.insert(0, line);
            totalLength += line.length();
        }

        return sb.toString();
    }
}
