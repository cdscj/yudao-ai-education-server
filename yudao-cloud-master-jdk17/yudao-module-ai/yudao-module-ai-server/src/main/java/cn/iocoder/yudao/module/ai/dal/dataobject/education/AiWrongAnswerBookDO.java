package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

@TableName("ai_wrong_answer_book")
@KeySequence("ai_wrong_answer_book_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiWrongAnswerBookDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private Long questionId;
    private Long subjectId;
    private String knowledgeTagIds;
    private String userAnswer;
    private String correctAnswer;
    private Boolean isCorrect;
    private String errorType;
    private String errorAnalysis;
    private Integer reviewCount;
    private LocalDateTime lastReviewTime;
    private LocalDateTime nextReviewTime;
    private Integer masteryLevel;
    private String sourceType;
    private Long sourceId;
}
