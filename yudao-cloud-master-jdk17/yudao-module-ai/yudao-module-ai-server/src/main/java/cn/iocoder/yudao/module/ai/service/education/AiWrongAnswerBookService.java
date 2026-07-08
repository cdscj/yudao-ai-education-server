package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;

import java.util.List;
import java.util.Map;

public interface AiWrongAnswerBookService {

    void recordAnswer(AiWrongAnswerBookDO record);

    PageResult<AiWrongAnswerBookDO> getWrongAnswerPage(Long userId, Long subjectId, Integer masteryLevel,
                                                        Integer pageNo, Integer pageSize);

    AiWrongAnswerBookDO getWrongAnswer(Long id);

    void reviewQuestion(Long id);

    Map<String, Object> getStats(Long userId);

    /**
     * 获取学生的薄弱知识点分析
     *
     * @param userId    用户 ID
     * @param subjectId 学科 ID（可选）
     * @return 薄弱点列表，按错误次数降序排列
     */
    List<WeakPointVO> getWeakPointAnalysis(Long userId, Long subjectId);

    /**
     * 薄弱知识点 VO
     */
    record WeakPointVO(Long tagId, String tagName, int wrongCount, int masteryLevel) {}
}
