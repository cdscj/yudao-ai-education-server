package cn.iocoder.yudao.module.ai.framework.ai.core.memory;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatConversationDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 对话记忆管理器 — 自动摘要 + 上下文压缩
 *
 * <p>当历史消息 token 数超过阈值时，自动生成摘要压缩旧消息，
 * 保留最近 N 轮完整消息，确保上下文不超出模型 token 限制。</p>
 *
 * @author yudao
 */
@Component
@Slf4j
public class ConversationMemoryManager {

    @Resource
    private AiLlmService llmService;

    @Resource
    private MemoryConfig memoryConfig;

    /** 触发摘要的 token 阈值（估算：1 汉字≈2 tokens） */
    private static final int SUMMARY_THRESHOLD_CHARS = 4000;

    /** 摘要后保留的最近消息轮数 */
    private static final int KEEP_RECENT_ROUNDS = 5;

    private static final String SUMMARY_PROMPT = """
            你是一个对话摘要助手。请将以下对话历史压缩为一段简洁的摘要，
            保留关键信息：用户的问题、核心事实、重要决策、用户偏好。
            只输出摘要内容，不要添加任何解释。""";

    /**
     * 管理对话记忆：必要时生成摘要，返回优化后的上下文
     *
     * @param conversation 对话对象
     * @param messages     消息列表（按时间排序）
     * @return 记忆上下文（摘要 + 最近消息）
     */
    public MemoryContext manageMemory(AiChatConversationDO conversation,
                                       List<AiChatMessageDO> messages) {
        if (messages == null || messages.isEmpty()) {
            return new MemoryContext("", messages);
        }

        // 估算总字符数
        int totalChars = messages.stream()
                .mapToInt(m -> StrUtil.length(m.getContent()))
                .sum();

        // 未超阈值：直接返回
        if (totalChars < SUMMARY_THRESHOLD_CHARS) {
            return new MemoryContext(
                    StrUtil.nullToDefault(conversation.getSummary(), ""),
                    messages);
        }

        // 需要摘要：分离旧消息和最近消息
        int recentStart = Math.max(0, messages.size() - KEEP_RECENT_ROUNDS * 2);
        List<AiChatMessageDO> oldMessages = messages.subList(0, recentStart);
        List<AiChatMessageDO> recentMessages = messages.subList(recentStart, messages.size());

        // 生成摘要
        String existingSummary = StrUtil.nullToDefault(conversation.getSummary(), "");
        String newSummary = generateSummary(oldMessages, existingSummary);

        log.debug("[MemoryManager] 生成摘要: 旧消息数={}, 摘要长度={}",
                oldMessages.size(), StrUtil.length(newSummary));

        return new MemoryContext(newSummary, recentMessages);
    }

    /**
     * 生成对话摘要
     */
    public String generateSummary(List<AiChatMessageDO> messages, String existingSummary) {
        if (messages == null || messages.isEmpty()) {
            return existingSummary;
        }

        // 构建待摘要的对话文本
        StringBuilder dialog = new StringBuilder();
        if (StrUtil.isNotBlank(existingSummary)) {
            dialog.append("【已有摘要】").append(existingSummary).append("\n\n【新增对话】\n");
        }
        for (AiChatMessageDO msg : messages) {
            String role = "user".equals(msg.getType()) ? "用户" : "助手";
            dialog.append(role).append(": ")
                    .append(StrUtil.maxLength(msg.getContent(), 500))
                    .append("\n");
        }

        try {
            String summary = llmService.chatSync(SUMMARY_PROMPT, dialog.toString(), "");
            return summary != null ? summary.trim() : existingSummary;
        } catch (Exception e) {
            log.warn("[MemoryManager] 摘要生成失败: {}", e.getMessage());
            return existingSummary;
        }
    }

    /**
     * 记忆上下文
     */
    public record MemoryContext(String summary, List<AiChatMessageDO> recentMessages) {
        public boolean hasSummary() {
            return StrUtil.isNotBlank(summary);
        }
    }
}
