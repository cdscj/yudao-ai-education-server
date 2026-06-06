package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamRecordDO;

public interface AiExamService {
    Long createExam(AiExamDO exam);
    void updateExam(AiExamDO exam);
    void deleteExam(Long id);
    void publishExam(Long id);
    AiExamDO getExam(Long id);
    PageResult<AiExamDO> getExamPage(Long subjectId, String publishStatus, String title, Integer pageNo, Integer pageSize);
    PageResult<AiExamDO> getPublishedList(Integer pageNo, Integer pageSize);
    Long startExam(Long examId, Long userId);
    void submitExam(Long examId, Long userId, String answers, Integer durationSeconds);
    AiExamRecordDO getRecord(Long examId, Long userId);
    PageResult<AiExamRecordDO> getRecordPage(Long userId, Integer pageNo, Integer pageSize);
    /**
     * AI 生成考试题目
     *
     * @param examId  考试编号（生成后关联到此考试）
     * @param subjectId 学科编号
     * @param difficulty 难度 (1-5)
     * @param questionCount 题目数量
     */
    void generateExamByAI(Long examId, Long subjectId, Integer difficulty, Integer questionCount);
    /**
     * AI 分析考试记录（错题分析 + 薄弱点诊断）
     *
     * @param recordId 考试记录编号
     */
    void analyzeExamRecord(Long recordId);
}
