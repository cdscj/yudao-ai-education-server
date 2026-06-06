package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;

import java.util.Map;

public interface AiWrongAnswerBookService {

    void recordAnswer(AiWrongAnswerBookDO record);

    PageResult<AiWrongAnswerBookDO> getWrongAnswerPage(Long userId, Long subjectId, Integer masteryLevel,
                                                        Integer pageNo, Integer pageSize);

    AiWrongAnswerBookDO getWrongAnswer(Long id);

    void reviewQuestion(Long id);

    Map<String, Object> getStats(Long userId);
}
