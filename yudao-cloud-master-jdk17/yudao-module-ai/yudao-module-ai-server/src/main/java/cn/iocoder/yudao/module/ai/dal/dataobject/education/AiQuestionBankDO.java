package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("ai_question_bank")
@KeySequence("ai_question_bank_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQuestionBankDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long subjectId;
    private String knowledgeTagIds;
    private String questionType;
    private Integer difficulty;
    private String title;
    private String content;
    private String options;
    private String answer;
    private String analysis;
    private String programmingConfig;
    private String source;
    private String aiGeneratedPrompt;
    private Integer status;
    private Integer sort;
}
