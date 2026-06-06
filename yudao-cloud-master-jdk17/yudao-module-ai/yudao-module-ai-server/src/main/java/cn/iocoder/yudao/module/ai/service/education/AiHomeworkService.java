package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkSubmissionDO;

public interface AiHomeworkService {
    Long createHomework(AiHomeworkDO homework);
    void updateHomework(AiHomeworkDO homework);
    void deleteHomework(Long id);
    void publishHomework(Long id);
    AiHomeworkDO getHomework(Long id);
    PageResult<AiHomeworkDO> getHomeworkPage(Long subjectId, String publishStatus, String title, Integer pageNo, Integer pageSize);
    PageResult<AiHomeworkDO> getPublishedPage(Long subjectId, Integer pageNo, Integer pageSize);
    Long submitHomework(Long homeworkId, Long userId, String answers, Integer durationSeconds);
    AiHomeworkSubmissionDO getSubmission(Long homeworkId, Long userId);
    PageResult<AiHomeworkSubmissionDO> getSubmissionPage(Long homeworkId, Long userId, String gradeStatus, Integer pageNo, Integer pageSize);
    /**
     * AI 批改提交
     *
     * @param submissionId 提交编号
     */
    void gradeSubmission(Long submissionId);
}
