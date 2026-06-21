package cn.iocoder.yudao.module.ai.dal.mysql.knowledge;

import cn.iocoder.yudao.module.ai.dal.dataobject.knowledge.AiKnowledgeSegmentDocument;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * AI 知识库段落 ES Repository — 提供 BM25 关键词检索
 *
 * @author yudao
 */
public interface AiKnowledgeSegmentRepository
        extends ElasticsearchRepository<AiKnowledgeSegmentDocument, String> {

    /**
     * BM25 关键词检索（限制知识库范围）
     *
     * @param query       搜索关键词
     * @param knowledgeId 知识库 ID
     * @return 搜索结果（含 BM25 分数）
     */
    @Query("{\"bool\":{\"must\":[{\"match\":{\"content\":\"?0\"}},{\"term\":{\"knowledgeId\":\"?1\"}}]}}")
    SearchHits<AiKnowledgeSegmentDocument> searchByContentAndKnowledgeId(String query, String knowledgeId);
}
