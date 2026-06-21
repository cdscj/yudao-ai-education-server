package cn.iocoder.yudao.module.ai.service.knowledge.rewrite;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiChatMessageDO;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * AI Query 改写器 — 在对话场景中改写用户问题
 *
 * <p>解决 "那个是什么?" → 指代消解为 "xxx是什么?"
 * 解决 "继续说" → 理解上下文后续请求</p>
 *
 * @author yudao
 */
@Service
@Slf4j
public class AiQueryRewriter {

    @Resource
    private AiLlmService llmService;

    private static final String REWRITE_PROMPT = """
            你是一个查询改写助手。根据对话历史，将用户当前的问题改写为独立、完整的查询。

            规则：
            1. 消除指代词（"它""那个""这个"→具体实体）
            2. 补充省略的主语/宾语
            3. 如果问题已经很完整，直接原样返回
            4. 只输出改写后的查询，不要任何解释

            对话历史：
            %s

            当前问题：%s

            改写后：""";

    /**
     * 改写查询（同步）
     *
     * @param query          用户当前查询
     * @param historyMessages 对话历史消息
     * @return 改写后的查询
     */
    public String rewrite(String query, List<AiChatMessageDO> historyMessages) {
        // 简单场景不需要改写
        if (StrUtil.isBlank(query) || query.length() > 200) {
            return query;
        }
        // 无历史消息时不需要改写
        if (CollUtil.isEmpty(historyMessages)) {
            return query;
        }

        // 构建历史摘要
        StringBuilder history = new StringBuilder();
        int maxHistory = Math.min(historyMessages.size(), 6); // 最多取最近3轮
        int start = Math.max(0, historyMessages.size() - maxHistory);
        for (int i = start; i < historyMessages.size(); i++) {
            AiChatMessageDO msg = historyMessages.get(i);
            String role = "user".equals(msg.getType()) ? "用户" : "助手";
            String text = StrUtil.maxLength(msg.getContent(), 200);
            history.append(role).append(": ").append(text).append("\n");
        }

        String prompt = String.format(REWRITE_PROMPT, history.toString(), query);
        try {
            String rewritten = llmService.chatSync(
                    "你是查询改写助手，只输出改写后的查询。", prompt, "");
            String result = rewritten != null ? rewritten.trim() : query;
            // 如果改写结果太长或太短，使用原始查询
            if (result.length() < 2 || result.length() > 500) {
                return query;
            }
            if (!result.equals(query)) {
                log.debug("[QueryRewriter] 改写: \"{}\" → \"{}\"", query, result);
            }
            return result;
        } catch (Exception e) {
            log.warn("[QueryRewriter] 改写失败，使用原始查询: {}", e.getMessage());
            return query;
        }
    }
}
