package cn.iocoder.yudao.module.ai.framework.ai.core.observability;

import java.util.Map;

/**
 * AI 模型定价表 — 用于估算每次调用的费用
 *
 * <p>价格单位：美元/百万 tokens（USD per 1M tokens）。
 * 数据来源：各平台官方定价页面（2024-2025），实际价格以各平台最新公告为准。</p>
 *
 * @author yudao
 */
public class AiModelPricing {

    private static final Map<String, double[]> PRICES = Map.ofEntries(
            // OpenAI
            Map.entry("gpt-4o",           new double[]{2.50, 10.00}),
            Map.entry("gpt-4o-mini",      new double[]{0.15, 0.60}),
            Map.entry("gpt-4-turbo",      new double[]{10.00, 30.00}),
            Map.entry("gpt-3.5-turbo",    new double[]{0.50, 1.50}),
            // Anthropic
            Map.entry("claude-3-5-sonnet", new double[]{3.00, 15.00}),
            Map.entry("claude-3-opus",    new double[]{15.00, 75.00}),
            Map.entry("claude-3-haiku",   new double[]{0.25, 1.25}),
            // DeepSeek
            Map.entry("deepseek-chat",    new double[]{0.14, 0.28}),
            Map.entry("deepseek-reasoner",new double[]{0.55, 2.19}),
            // 通义千问
            Map.entry("qwen-turbo",       new double[]{0.30, 0.60}),
            Map.entry("qwen-plus",        new double[]{0.50, 2.00}),
            Map.entry("qwen-max",         new double[]{2.00, 6.00}),
            // 文心一言
            Map.entry("ernie-4.0",        new double[]{2.00, 8.00}),
            Map.entry("ernie-3.5",        new double[]{0.12, 0.48}),
            // 智谱 GLM
            Map.entry("glm-4",            new double[]{1.50, 6.00}),
            Map.entry("glm-3-turbo",      new double[]{0.10, 0.40}),
            // 豆包
            Map.entry("doubao-pro",       new double[]{0.50, 2.00}),
            Map.entry("doubao-lite",      new double[]{0.10, 0.40}),
            // 混元
            Map.entry("hunyuan-pro",      new double[]{1.00, 4.00}),
            // 月之暗面
            Map.entry("moonshot-v1",      new double[]{1.00, 2.00}),
            // MiniMax
            Map.entry("abab6.5s",         new double[]{0.50, 1.00}),
            // Gemini
            Map.entry("gemini-2.0-flash", new double[]{0.10, 0.40})
    );

    /**
     * 估算调用费用
     *
     * @param modelName       模型名称
     * @param promptTokens     输入 token 数
     * @param completionTokens 输出 token 数
     * @return 估算费用（美元），未找到价格则返回 0
     */
    public static double estimateCost(String modelName, int promptTokens, int completionTokens) {
        if (modelName == null) return 0;
        double[] prices = PRICES.get(modelName.toLowerCase());
        if (prices == null || prices.length < 2) {
            // 模糊匹配
            for (Map.Entry<String, double[]> entry : PRICES.entrySet()) {
                if (modelName.toLowerCase().contains(entry.getKey())) {
                    prices = entry.getValue();
                    break;
                }
            }
        }
        if (prices == null || prices.length < 2) return 0;
        return (promptTokens / 1_000_000.0) * prices[0]
             + (completionTokens / 1_000_000.0) * prices[1];
    }
}
