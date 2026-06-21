package cn.iocoder.yudao.module.ai.dal.dataobject.knowledge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * AI 知识库段落 ES 文档 — 用于 BM25 关键词检索
 *
 * <p>与向量检索互补：向量擅长语义匹配，BM25 擅长精确关键词匹配。
 * 文档入库时同步写入 ES，检索时向量 + ES 双路融合。</p>
 *
 * @author yudao
 */
@Document(indexName = "ai_knowledge_segments")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiKnowledgeSegmentDocument {

    /**
     * ES 文档 ID（与向量库 vectorId 对应）
     */
    @Id
    private String id;

    /**
     * 段落内容（IK 中文分词 + BM25 索引）
     */
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    /**
     * 知识库编号
     */
    @Field(type = FieldType.Keyword)
    private String knowledgeId;

    /**
     * 文档编号
     */
    @Field(type = FieldType.Keyword)
    private String documentId;

    /**
     * 段落编号（对应 ai_knowledge_segment 表的主键）
     */
    @Field(type = FieldType.Long)
    private Long segmentId;
}
