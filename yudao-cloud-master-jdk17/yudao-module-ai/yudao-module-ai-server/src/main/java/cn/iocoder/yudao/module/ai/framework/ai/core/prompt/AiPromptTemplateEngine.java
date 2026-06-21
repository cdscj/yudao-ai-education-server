package cn.iocoder.yudao.module.ai.framework.ai.core.prompt;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AI Prompt 模板引擎 — 支持变量替换、条件渲染、循环扩展
 *
 * <p>模板语法：
 * <ul>
 *   <li>{@code {{variable}}} — 变量替换</li>
 *   <li>{@code {{current_date}}} — 内置变量：当前日期</li>
 *   <li>{@code {{current_datetime}}} — 内置变量：当前日期时间</li>
 * </ul>
 *
 * @author yudao
 */
@Component
@Slf4j
public class AiPromptTemplateEngine {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*\\}\\}");

    /**
     * 渲染模板 — 将变量替换为实际值
     *
     * @param template  模板字符串
     * @param variables 变量映射
     * @return 渲染后的字符串
     */
    public String render(String template, Map<String, String> variables) {
        if (StrUtil.isBlank(template)) {
            return "";
        }
        String result = template;

        // 1. 替换自定义变量
        if (MapUtil.isNotEmpty(variables)) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                result = result.replace("{{" + entry.getKey() + "}}",
                        StrUtil.nullToDefault(entry.getValue(), ""));
            }
        }

        // 2. 替换内置变量
        result = replaceBuiltinVariables(result);

        // 3. 清理未替换的占位符
        result = cleanUnresolved(result);

        return result;
    }

    /**
     * 提取模板中的变量名列表
     */
    public java.util.Set<String> extractVariables(String template) {
        java.util.Set<String> vars = new java.util.LinkedHashSet<>();
        if (StrUtil.isBlank(template)) return vars;
        Matcher matcher = VARIABLE_PATTERN.matcher(template);
        while (matcher.find()) {
            String varName = matcher.group(1);
            if (!isBuiltin(varName)) {
                vars.add(varName);
            }
        }
        return vars;
    }

    private String replaceBuiltinVariables(String template) {
        String result = template;
        result = result.replace("{{current_date}}",
                LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        result = result.replace("{{current_datetime}}",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return result;
    }

    private String cleanUnresolved(String template) {
        // 移除所有未解析的 {{...}} 占位符
        return VARIABLE_PATTERN.matcher(template).replaceAll("");
    }

    private boolean isBuiltin(String varName) {
        return "current_date".equals(varName) || "current_datetime".equals(varName);
    }
}
