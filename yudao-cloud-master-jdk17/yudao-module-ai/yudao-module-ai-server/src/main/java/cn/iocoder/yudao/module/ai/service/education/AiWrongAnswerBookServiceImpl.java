package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiKnowledgeTagMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiWrongAnswerBookMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.WRONG_ANSWER_NOT_EXISTS;

@Service
@Validated
public class AiWrongAnswerBookServiceImpl implements AiWrongAnswerBookService {

    @Resource
    private AiWrongAnswerBookMapper mapper;
    @Resource
    private AiKnowledgeTagMapper knowledgeTagMapper;

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

    @Override
    public List<WeakPointVO> getWeakPointAnalysis(Long userId, Long subjectId) {
        // 1. 获取用户所有错题
        PageResult<AiWrongAnswerBookDO> pageResult = mapper.selectPage(
                new PageParam().setPageNo(1).setPageSize(1000), userId, subjectId, null);
        List<AiWrongAnswerBookDO> wrongList = pageResult.getList();
        if (CollUtil.isEmpty(wrongList)) {
            return List.of();
        }

        // 2. 按知识点标签聚合统计
        Map<Long, int[]> tagStats = new LinkedHashMap<>(); // tagId -> [wrongCount, totalMastery]
        for (AiWrongAnswerBookDO w : wrongList) {
            String tagIdsStr = w.getKnowledgeTagIds();
            if (StrUtil.isBlank(tagIdsStr)) continue;
            try {
                String cleaned = tagIdsStr.replace("[", "").replace("]", "").replace("\"", "");
                String[] parts = cleaned.split(",");
                for (String part : parts) {
                    Long tagId = Long.parseLong(part.trim());
                    int[] stats = tagStats.computeIfAbsent(tagId, k -> new int[2]);
                    stats[0]++; // wrongCount
                    stats[1] += w.getMasteryLevel() != null ? w.getMasteryLevel() : 0; // accumulate mastery
                }
            } catch (Exception e) {
                // ignore parse errors for individual records
            }
        }

        // 3. 批量查标签名称
        List<Long> tagIds = new ArrayList<>(tagStats.keySet());
        final Map<Long, String> tagNameMap;
        if (!CollUtil.isEmpty(tagIds)) {
            List<AiKnowledgeTagDO> tags = knowledgeTagMapper.selectListByIds(tagIds);
            tagNameMap = tags.stream().collect(Collectors.toMap(AiKnowledgeTagDO::getId, AiKnowledgeTagDO::getName, (a, b) -> a));
        } else {
            tagNameMap = Map.of();
        }

        // 4. 构建结果，按错误次数降序排列
        return tagStats.entrySet().stream()
                .map(e -> {
                    Long tagId = e.getKey();
                    int[] s = e.getValue();
                    return new WeakPointVO(tagId,
                            tagNameMap.getOrDefault(tagId, "知识点#" + tagId),
                            s[0],
                            s[0] > 0 ? s[1] / s[0] : 0);
                })
                .sorted((a, b) -> Integer.compare(b.wrongCount(), a.wrongCount()))
                .collect(Collectors.toList());
    }
}
