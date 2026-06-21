package cn.iocoder.yudao.module.ai.service.knowledge.search;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDO;
import cn.iocoder.yudao.module.ai.service.knowledge.bo.AiKnowledgeSegmentSearchReqBO;
import cn.iocoder.yudao.module.ai.service.knowledge.bo.AiKnowledgeSegmentSearchRespBO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 混合检索服务 — BM25 + 向量检索融合
 *
 * <p>使用 RRF (Reciprocal Rank Fusion) 算法融合两种检索方式的排序结果：
 * <pre>
 *   RRF(d) = Σ 1/(k + rank_i(d))
 *   其中 k=60, rank_i 是文档在第 i 种检索方式中的排名
 * </pre>
 *
 * @author yudao
 */
@Service
@Slf4j
public class HybridSearchService {

    private static final double RRF_K = 60.0;

    /**
     * 混合检索
     *
     * @param vectorResults 向量检索结果（已按相似度排序）
     * @param bm25Results   BM25 检索结果（已按分数排序）
     * @param topK          最终返回数量
     * @param vectorWeight  向量权重 (0.0-1.0)，BM25 权重 = 1 - vectorWeight
     * @return 融合后的检索结果
     */
    public List<AiKnowledgeSegmentSearchRespBO> hybridSearch(
            List<AiKnowledgeSegmentSearchRespBO> vectorResults,
            List<AiKnowledgeSegmentSearchRespBO> bm25Results,
            int topK, double vectorWeight) {

        if (CollUtil.isEmpty(vectorResults) && CollUtil.isEmpty(bm25Results)) {
            return Collections.emptyList();
        }

        // 构建 RRF 分数映射
        Map<Long, Double> rrfScores = new LinkedHashMap<>();

        // 向量检索的 RRF 分数
        for (int i = 0; i < vectorResults.size(); i++) {
            Long id = vectorResults.get(i).getId();
            double rrf = vectorWeight / (RRF_K + i + 1);
            rrfScores.merge(id, rrf, Double::sum);
        }

        // BM25 检索的 RRF 分数
        double bm25Weight = 1.0 - vectorWeight;
        for (int i = 0; i < bm25Results.size(); i++) {
            Long id = bm25Results.get(i).getId();
            double rrf = bm25Weight / (RRF_K + i + 1);
            rrfScores.merge(id, rrf, Double::sum);
        }

        // 构建 ID 到结果的映射
        Map<Long, AiKnowledgeSegmentSearchRespBO> idToResult = new LinkedHashMap<>();
        for (AiKnowledgeSegmentSearchRespBO r : vectorResults) {
            idToResult.putIfAbsent(r.getId(), r);
        }
        for (AiKnowledgeSegmentSearchRespBO r : bm25Results) {
            idToResult.putIfAbsent(r.getId(), r);
        }

        // 按 RRF 分数排序并取 topK
        return rrfScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(topK)
                .map(entry -> {
                    AiKnowledgeSegmentSearchRespBO result = idToResult.get(entry.getKey());
                    if (result != null) {
                        result.setScore(entry.getValue());
                    }
                    return result;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
