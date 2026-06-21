package cn.iocoder.yudao.module.ai.framework.ai.core.structured;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.framework.ai.core.llm.AiLlmService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 结构化输出服务 — 约束 LLM 按 JSON Schema 输出
 *
 * <p>实现策略：
 * <ol>
 *   <li>在 Prompt 中注入 Schema 约束指令</li>
 *   <li>LLM 返回后尝试 JSON 解析</li>
 *   <li>解析失败自动重试（最多3次）</li>
 *   <li>3 次均失败则返回 null</li>
 * </ol>
 *
 * @author yudao
 */
@Service
@Slf4j
public class StructuredOutputService {

    @Resource
    private AiLlmService llmService;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final int MAX_RETRIES = 3;

    /**
     * 调用 LLM 并约束输出为指定 JSON Schema
     *
     * @param systemPrompt 系统提示词
     * @param userInput    用户输入
     * @param jsonSchema   JSON Schema 描述（如: {"type":"object","properties":{"name":{"type":"string"}}}）
     * @return 解析后的 JsonNode，失败返回 null
     */
    public JsonNode callWithSchema(String systemPrompt, String userInput, String jsonSchema) {
        String schemaInstruction = buildSchemaInstruction(jsonSchema);
        String fullSystemPrompt = systemPrompt + "\n\n" + schemaInstruction;

        String lastError = null;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                String rawResponse = llmService.chatSync(fullSystemPrompt, userInput, "");
                String json = extractJson(rawResponse);

                JsonNode node = OBJECT_MAPPER.readTree(json);
                log.debug("[StructuredOutput] 第 {} 次尝试成功", attempt);
                return node;
            } catch (Exception e) {
                lastError = e.getMessage();
                log.warn("[StructuredOutput] 第 {} 次解析失败: {}", attempt, e.getMessage());
                // 下次重试时追加错误提示
                fullSystemPrompt = systemPrompt + "\n\n" + schemaInstruction
                        + "\n\n【上次输出格式错误：" + lastError + "，请严格按JSON格式输出】";
            }
        }

        log.error("[StructuredOutput] {} 次重试均失败: {}", MAX_RETRIES, lastError);
        return null;
    }

    private String buildSchemaInstruction(String jsonSchema) {
        return String.format("""
                【重要】你必须严格按以下 JSON Schema 格式返回，不要包含任何其他文字：
                %s

                返回示例要求：
                - 必须是合法的 JSON
                - 不要用 markdown 代码块包裹
                - 直接输出 JSON 对象
                """, jsonSchema);
    }

    private String extractJson(String raw) {
        if (StrUtil.isBlank(raw)) return "{}";

        String trimmed = raw.trim();

        // 去除 markdown 代码块
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.substring(7);
        }
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.substring(3);
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.substring(0, trimmed.length() - 3);
        }
        trimmed = trimmed.trim();

        // 查找 JSON 对象边界
        int start = trimmed.indexOf('{');
        int end = trimmed.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }
        // 尝试 JSON 数组
        start = trimmed.indexOf('[');
        end = trimmed.lastIndexOf(']');
        if (start >= 0 && end > start) {
            return trimmed.substring(start, end + 1);
        }

        return trimmed;
    }
}
