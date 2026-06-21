package cn.iocoder.yudao.module.ai.service.knowledge.rerank;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.ai.service.knowledge.bo.AiKnowledgeSegmentSearchRespBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AI Reranker 重排序服务
 *
 * <p>当前实现：基于查询词与段落内容的 Jaccard 相似度 + 段落长度偏好的简单重排。
 * 生产环境可替换为：
 * <ul>
 *   <li>Cohere Rerank API</li>
 *   <li>Jina Reranker API</li>
 *   <li>本地 BGE-Reranker 模型 (ONNX Runtime)</li>
 *   <li>Cross-encoder 模型</li>
 * </ul></p>
 *
 * @author yudao
 */
@Service
@Slf4j
public class AiRerankerService {

    /**
     * 对检索结果进行重排序
     *
     * @param query   原始查询
     * @param results 待排序的结果列表
     * @param topK    返回前 K 个结果
     * @return 重排序后的结果
     */
    public List<AiKnowledgeSegmentSearchRespBO> rerank(String query,
                                                        List<AiKnowledgeSegmentSearchRespBO> results,
                                                        int topK) {
        if (CollUtil.isEmpty(results)) {
            return results;
        }

        // 计算每个段落的分数：Jaccard 词重叠度 × 段落长度加分
        return results.stream()
                .map(r -> {
                    double score = computeRelevanceScore(query, r.getContent());
                    r.setScore(score);
                    return r;
                })
                .sorted(Comparator.comparingDouble(AiKnowledgeSegmentSearchRespBO::getScore).reversed())
                .limit(Math.min(topK, results.size()))
                .collect(Collectors.toList());
    }

    /**
     * 计算查询与段落的相关性分数
     *
     * <p>使用 Jaccard 相似度：重叠词数 / 并集词数</p>
     */
    private double computeRelevanceScore(String query, String content) {
        if (query == null || content == null || query.isEmpty() || content.isEmpty()) {
            return 0.0;
        }

        // 分词
        java.util.Set<String> queryTokens = tokenize(query);
        java.util.Set<String> contentTokens = tokenize(content);

        // Jaccard
        java.util.Set<String> intersection = new java.util.HashSet<>(queryTokens);
        intersection.retainAll(contentTokens);

        java.util.Set<String> union = new java.util.HashSet<>(queryTokens);
        union.addAll(contentTokens);

        double jaccard = union.isEmpty() ? 0.0
                : (double) intersection.size() / union.size();

        // 长度惩罚：过短的段落通常不够完整
        double lengthBonus = Math.min(1.0, content.length() / 200.0);

        return jaccard * 0.7 + lengthBonus * 0.3;
    }

    private java.util.Set<String> tokenize(String text) {
        // 简单分词：按非字母数字字符分割，取长度>=2的词
        String[] words = text.toLowerCase().split("[^\\u4e00-\\u9fa5a-zA-Z0-9]+");
        java.util.Set<String> tokens = new java.util.HashSet<>();
        for (String w : words) {
            if (w.length() >= 1) {
                tokens.add(w);
            }
        }
        return tokens;
    }
}
