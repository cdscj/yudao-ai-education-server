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

    List<AiQuestionBankDO> getQuestionListByIds(List<Long> ids);
}
