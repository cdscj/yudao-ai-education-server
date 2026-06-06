package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiWrongAnswerBookMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.WRONG_ANSWER_NOT_EXISTS;

@Service
@Validated
public class AiWrongAnswerBookServiceImpl implements AiWrongAnswerBookService {

    @Resource
    private AiWrongAnswerBookMapper mapper;

    @Override
    public void recordAnswer(AiWrongAnswerBookDO record) {
        AiWrongAnswerBookDO existing = mapper.selectByUserIdAndQuestionId(record.getUserId(), record.getQuestionId());
        if (existing != null) {
            existing.setUserAnswer(record.getUserAnswer());
            existing.setIsCorrect(record.getIsCorrect());
            existing.setErrorType(record.getErrorType());
            existing.setSourceType(record.getSourceType());
            existing.setSourceId(record.getSourceId());
            if (!record.getIsCorrect()) {
                existing.setReviewCount(existing.getReviewCount() + 1);
                existing.setLastReviewTime(LocalDateTime.now());
                existing.setMasteryLevel(Math.min(existing.getMasteryLevel() + 1, 5));
            }
            mapper.updateById(existing);
        } else {
            record.setReviewCount(record.getIsCorrect() ? 0 : 1);
            record.setLastReviewTime(record.getIsCorrect() ? null : LocalDateTime.now());
            record.setMasteryLevel(record.getIsCorrect() ? 5 : 0);
            mapper.insert(record);
        }
    }

    @Override
    public PageResult<AiWrongAnswerBookDO> getWrongAnswerPage(Long userId, Long subjectId, Integer masteryLevel,
                                                               Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), userId, subjectId, masteryLevel);
    }

    @Override
    public AiWrongAnswerBookDO getWrongAnswer(Long id) {
        AiWrongAnswerBookDO record = mapper.selectById(id);
        if (record == null) {
            throw exception(WRONG_ANSWER_NOT_EXISTS);
        }
        return record;
    }

    @Override
    public void reviewQuestion(Long id) {
        AiWrongAnswerBookDO record = getWrongAnswer(id);
        record.setReviewCount(record.getReviewCount() + 1);
        record.setLastReviewTime(LocalDateTime.now());
        record.setNextReviewTime(LocalDateTime.now().plusDays(1));
        record.setMasteryLevel(Math.min(record.getMasteryLevel() + 1, 5));
        mapper.updateById(record);
    }

    @Override
    public Map<String, Object> getStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCount", mapper.selectCountByUserId(userId));
        return stats;
    }
}
