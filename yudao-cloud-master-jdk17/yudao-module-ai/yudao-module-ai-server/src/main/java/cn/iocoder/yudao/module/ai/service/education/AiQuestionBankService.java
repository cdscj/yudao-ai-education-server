package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;

import java.util.List;

public interface AiQuestionBankService {

    Long createQuestion(AiQuestionBankDO question);

    void updateQuestion(AiQuestionBankDO question);

    void deleteQuestion(Long id);

    AiQuestionBankDO getQuestion(Long id);

    PageResult<AiQuestionBankDO> getQuestionPage(Long subjectId, String questionType, Integer difficulty,
                                                  String keyword, Integer status, Integer pageNo, Integer pageSize);

    /**
     * 根据学科ID列表分页查询题目（支持父学科自动包含子学科）
     */
    PageResult<AiQuestionBankDO> getQuestionPageBySubjectIds(java.util.Collection<Long> subjectIds, String questionType,
                                                              Integer difficulty, String keyword, Integer status,
                                                              Integer pageNo, Integer pageSize);

    List<AiQuestionBankDO> getQuestionListByIds(List<Long> ids);

    /**
     * AI 生成题目并保存到题库
     *
     * @param subjectId        学科 ID
     * @param questionType     题目类型 (CHOICE/JUDGE/SHORT_ANSWER)
     * @param difficulty       难度 (1-5)
     * @param count            生成数量
     * @param keyword          关键词（可选）
     * @param knowledgeTagIds  知识点标签 ID 列表，JSON 数组字符串如 "[1,2,3]"（可选）
     * @return 生成的题目数量
     */
    int generateQuestionByAI(Long subjectId, String questionType, Integer difficulty, Integer count, String keyword, String knowledgeTagIds);
}
