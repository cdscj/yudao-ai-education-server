package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkSubmissionDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiHomeworkMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiHomeworkSubmissionMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiKnowledgeTagMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiQuestionBankMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.framework.ai.core.gateway.AiModelGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class AiHomeworkServiceImpl implements AiHomeworkService {

    @Resource private AiHomeworkMapper mapper;
    @Resource private AiHomeworkSubmissionMapper submissionMapper;
    @Resource private AiQuestionBankMapper questionBankMapper;
    @Resource private AiModelService modelService;
    @Resource private AiModelGateway modelGateway;
    @Resource private AiSystemConfigService configService;
    @Resource private AiWrongAnswerBookService wrongAnswerBookService;
    @Resource private AiKnowledgeTagMapper knowledgeTagMapper;
    @Resource private cn.iocoder.yudao.module.ai.service.social.AiUserActivityService userActivityService;

    @Override public Long createHomework(AiHomeworkDO h) { mapper.insert(h); return h.getId(); }
    @Override public void updateHomework(AiHomeworkDO h) { validateExists(h.getId()); mapper.updateById(h); }
    @Override public void deleteHomework(Long id) { validateExists(id); mapper.deleteById(id); }

    @Override
    public void publishHomework(Long id) {
        AiHomeworkDO h = validateExists(id);
        h.setPublishStatus("PUBLISHED");
        h.setPublishTime(LocalDateTime.now());
        mapper.updateById(h);
    }

    @Override public AiHomeworkDO getHomework(Long id) { return validateExists(id); }

    @Override
    public PageResult<AiHomeworkDO> getHomeworkPage(Long subjectId, String publishStatus, String title, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), subjectId, publishStatus, title);
    }

    @Override
    public PageResult<AiHomeworkDO> getPublishedPage(Long subjectId, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), subjectId, "PUBLISHED", null);
    }

    @Override
    @Transactional
    public Long submitHomework(Long homeworkId, Long userId, String answers, Integer durationSeconds) {
        AiHomeworkDO h = validateExists(homeworkId);
        if (!"PUBLISHED".equals(h.getPublishStatus())) throw exception(HOMEWORK_NOT_PUBLISHED);
        if (h.getDeadline() != null && LocalDateTime.now().isAfter(h.getDeadline())) throw exception(HOMEWORK_DEADLINE_PASSED);

        AiHomeworkSubmissionDO existing = submissionMapper.selectByHomeworkAndUser(homeworkId, userId);
        if (existing != null && existing.getGradeStatus().equals("GRADED") && !h.getAllowRedo())
            throw exception(HOMEWORK_ALREADY_SUBMITTED);

        AiHomeworkSubmissionDO sub = existing != null ? existing : new AiHomeworkSubmissionDO();
        sub.setHomeworkId(homeworkId).setUserId(userId).setAnswers(answers)
           .setDurationSeconds(durationSeconds).setSubmitTime(LocalDateTime.now())
           .setGradeStatus("UNGRADED").setRedoCount(existing != null ? existing.getRedoCount() + 1 : 0);
        if (existing != null) submissionMapper.updateById(sub);
        else submissionMapper.insert(sub);
        // 发布动态
        try {
            userActivityService.publishActivity(userId, 2, "完成了作业：" + h.getTitle(), sub.getId());
        } catch (Exception e) {
            log.warn("[submitHomework][发布动态失败，homeworkId({})]", homeworkId, e);
        }
        return sub.getId();
    }

    @Override
    public AiHomeworkSubmissionDO getSubmission(Long homeworkId, Long userId) {
        return submissionMapper.selectByHomeworkAndUser(homeworkId, userId);
    }

    @Override
    public PageResult<AiHomeworkSubmissionDO> getSubmissionPage(Long homeworkId, Long userId, String gradeStatus, Integer pageNo, Integer pageSize) {
        return submissionMapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), homeworkId, userId, gradeStatus);
    }

    private AiHomeworkDO validateExists(Long id) {
        AiHomeworkDO h = mapper.selectById(id);
        if (h == null) throw exception(HOMEWORK_NOT_EXISTS);
        return h;
    }

    private static final String DEFAULT_GRADE_PROMPT = """
            你是一位公正的作业批改老师。请根据以下信息批改学生的作业：

            批改要求：
            1. 逐题对比学生答案与参考答案，判断对错
            2. 客观题（选择、判断）按正确与否给分，主观题按要点给分
            3. 给出每题得分和反馈
            4. 为每道题识别涉及的知识点（用简短的中文标签，如"导数"、"积分"、"矩阵运算"）
            5. 总分保留整数

            输出 JSON 格式（严格只输出 JSON，不要有其他文字）：
            {"totalScore": 85, "aiScore": 85, "gradeDetail": "每题详细批改内容...", "aiFeedback": "整体评价和建议...", "perQuestion": [{"index":1,"correct":true,"score":10,"knowledgeTags":["导数定义","求导法则"]}, {"index":2,"correct":false,"score":0,"knowledgeTags":["定积分计算"]}]}
            """;

    @Override
    public void gradeSubmission(Long submissionId) {
        // 1. 获取提交和作业
        AiHomeworkSubmissionDO sub = submissionMapper.selectById(submissionId);
        if (sub == null) throw exception(HOMEWORK_SUBMISSION_NOT_EXISTS);
        AiHomeworkDO hw = validateExists(sub.getHomeworkId());

        // 2. 解析题目 ID 并查询题目
        List<String> answers = sub.getAnswers() != null ? Arrays.asList(sub.getAnswers().split("\n")) : List.of();
        List<AiQuestionBankDO> questions = List.of();
        if (hw.getQuestionIds() != null && !hw.getQuestionIds().isBlank()) {
            List<Long> qIds = Arrays.stream(hw.getQuestionIds().split(","))
                    .map(String::trim).filter(StrUtil::isNotBlank)
                    .map(Long::parseLong).collect(Collectors.toList());
            if (!CollUtil.isEmpty(qIds)) {
                questions = questionBankMapper.selectListByIds(qIds);
            }
        }

        // 3. 构建批改 prompt
        StringBuilder gradingInput = new StringBuilder();
        gradingInput.append("=== 作业信息 ===\n");
        gradingInput.append("标题：").append(hw.getTitle()).append("\n");
        gradingInput.append("总分：").append(hw.getTotalScore()).append("\n\n");

        gradingInput.append("=== 题目与答案 ===\n");
        for (int i = 0; i < questions.size(); i++) {
            AiQuestionBankDO q = questions.get(i);
            gradingInput.append("题").append(i + 1).append(") [").append(q.getQuestionType()).append("] ");
            gradingInput.append(q.getTitle()).append("\n");
            gradingInput.append("   参考答案：").append(q.getAnswer()).append("\n");
            if (i < answers.size()) {
                gradingInput.append("   学生答案：").append(answers.get(i)).append("\n");
            }
            gradingInput.append("\n");
        }

        // 4. 获取配置项
        String gradePrompt = configService.getConfigValue("edu.homework.grade.prompt", DEFAULT_GRADE_PROMPT);
        double objWeight = configService.getConfigInteger("edu.grade.weight.objective", 60) / 100.0;
        double subjWeight = configService.getConfigInteger("edu.grade.weight.subjective", 40) / 100.0;
        gradePrompt += "\n\n评分权重：客观题 " + objWeight + "，主观题 " + subjWeight;

        // 5. 调用 AI 批改
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) throw exception(MODEL_DEFAULT_NOT_EXISTS);

        AiModelDO model = models.get(models.size() - 1); // 使用最后一个（默认）模型

        String userMessage = gradePrompt + "\n" + gradingInput;
        String resultText = modelGateway.chatSync(model.getId(), new Prompt(userMessage));

        // 6. 解析 AI 返回结果并保存
        try {
            String cleanJson = resultText;
            if (cleanJson.contains("```json")) {
                cleanJson = cleanJson.substring(cleanJson.indexOf("```json") + 7);
            } else if (cleanJson.contains("```")) {
                cleanJson = cleanJson.substring(cleanJson.indexOf("```") + 3);
            }
            if (cleanJson.endsWith("```")) {
                cleanJson = cleanJson.substring(0, cleanJson.lastIndexOf("```"));
            }
            cleanJson = cleanJson.trim();
            if (cleanJson.startsWith("{")) {
                // 简化解析
                int score = parseIntFromJson(cleanJson, "totalScore");
                int aiScore = parseIntFromJson(cleanJson, "aiScore");
                String detail = extractStringFromJson(cleanJson, "gradeDetail");
                String feedback = extractStringFromJson(cleanJson, "aiFeedback");

                sub.setTotalScore(score > 0 ? score : aiScore);
                sub.setAiScore(aiScore > 0 ? aiScore : score);
                sub.setGradeDetail(StrUtil.blankToDefault(detail, resultText));
                sub.setAiFeedback(StrUtil.blankToDefault(feedback, ""));
                sub.setGradeStatus("GRADED");
            } else {
                // AI 返回的不是 JSON，直接保存原始响应
                sub.setGradeDetail(resultText);
                sub.setAiFeedback(resultText);
                sub.setGradeStatus("GRADED");
                sub.setTotalScore(0);
            }
        } catch (Exception e) {
            log.warn("[gradeSubmission][AI 返回格式解析失败，submissionId({})]", submissionId, e);
            sub.setGradeDetail(resultText);
            sub.setAiFeedback("");
            sub.setGradeStatus("GRADED");
            sub.setTotalScore(0);
        }
        submissionMapper.updateById(sub);

        // 7. 自动记录错题到错题本（附带知识点标签）
        try {
            autoRecordWrongAnswers(sub, hw, questions, answers, resultText);
        } catch (Exception e) {
            log.warn("[gradeSubmission][自动记录错题失败，submissionId({})]", submissionId, e);
        }
    }

    /**
     * 根据批改结果，自动将错题记录到错题本。如果题目还没有知识点标签，用 AI 识别的标签自动创建并回填。
     */
    private void autoRecordWrongAnswers(AiHomeworkSubmissionDO sub, AiHomeworkDO hw,
                                         List<AiQuestionBankDO> questions, List<String> answers, String aiResult) {
        // 1. 解析 AI 返回的 perQuestion，收集错题索引和 AI 识别的知识点标签
        java.util.Set<Integer> wrongIndices = new java.util.LinkedHashSet<>();
        java.util.Map<Integer, java.util.List<String>> aiTagMap = new java.util.LinkedHashMap<>(); // qIndex -> tagNames
        try {
            if (aiResult.contains("\"perQuestion\"")) {
                int start = aiResult.indexOf("\"perQuestion\"");
                int arrStart = aiResult.indexOf("[", start);
                int arrEnd = aiResult.indexOf("]", arrStart);
                if (arrStart > 0 && arrEnd > arrStart) {
                    String arrStr = aiResult.substring(arrStart, arrEnd + 1);
                    String[] items = arrStr.split("\\{");
                    for (String item : items) {
                        int idxVal = extractIntValue(item, "index");
                        if (idxVal <= 0) continue;
                        // 解析错题
                        if (item.contains("\"correct\"") && item.contains("false")) {
                            wrongIndices.add(idxVal);
                        }
                        // 解析 AI 识别的知识点标签
                        java.util.List<String> tags = extractStringArray(item, "knowledgeTags");
                        if (!tags.isEmpty()) {
                            aiTagMap.put(idxVal - 1, tags); // 转为 0-based index
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[autoRecordWrongAnswers][解析 perQuestion 失败，将使用简单匹配]", e);
        }

        // 2. AI 未返回逐题结果时，用简单答案对比兜底
        if (wrongIndices.isEmpty()) {
            for (int i = 0; i < questions.size() && i < answers.size(); i++) {
                AiQuestionBankDO q = questions.get(i);
                String studentAns = answers.get(i).trim();
                String refAns = q.getAnswer() != null ? q.getAnswer().trim() : "";
                if (studentAns.isEmpty() || (!refAns.isEmpty() && !studentAns.equals(refAns))) {
                    wrongIndices.add(i + 1);
                }
            }
        }

        // 3. 预加载该学科下已有的知识点标签（用于后续按名称查找/创建）
        java.util.Map<String, Long> existingTagNameToId = new java.util.HashMap<>();
        if (!aiTagMap.isEmpty()) {
            java.util.List<AiKnowledgeTagDO> existingTags = knowledgeTagMapper.selectListBySubjectId(hw.getSubjectId());
            for (AiKnowledgeTagDO t : existingTags) {
                existingTagNameToId.put(t.getName(), t.getId());
            }
        }

        // 4. 记录每条错题
        for (Integer idx : wrongIndices) {
            int qIndex = idx - 1;
            if (qIndex < 0 || qIndex >= questions.size()) continue;
            AiQuestionBankDO q = questions.get(qIndex);
            String studentAns = qIndex < answers.size() ? answers.get(qIndex).trim() : "";

            // 4a. 确定知识点标签 ID：题目已有就用，没有就尝试用 AI 识别的，再没有就空
            String tagIdsStr = q.getKnowledgeTagIds();
            if (StrUtil.isBlank(tagIdsStr)) {
                java.util.List<String> aiTags = aiTagMap.get(qIndex);
                if (aiTags != null && !aiTags.isEmpty()) {
                    java.util.List<Long> resolvedIds = resolveOrCreateTags(hw.getSubjectId(), aiTags, existingTagNameToId);
                    if (!resolvedIds.isEmpty()) {
                        tagIdsStr = resolvedIds.stream().map(String::valueOf).collect(Collectors.joining(","));
                        // 回填题库：让这道题以后都有标签
                        q.setKnowledgeTagIds(tagIdsStr);
                        questionBankMapper.updateById(q);
                        log.info("[autoRecordWrongAnswers][回填题目标签，questionId({}), tags({})]", q.getId(), tagIdsStr);
                    }
                }
            }

            AiWrongAnswerBookDO record = AiWrongAnswerBookDO.builder()
                    .userId(sub.getUserId())
                    .questionId(q.getId())
                    .subjectId(hw.getSubjectId())
                    .knowledgeTagIds(tagIdsStr)
                    .userAnswer(studentAns)
                    .correctAnswer(q.getAnswer())
                    .isCorrect(false)
                    .errorType("HOMEWORK_MISTAKE")
                    .sourceType("HOMEWORK")
                    .sourceId(sub.getHomeworkId())
                    .masteryLevel(0)
                    .build();
            wrongAnswerBookService.recordAnswer(record);
        }
        if (!wrongIndices.isEmpty()) {
            log.info("[autoRecordWrongAnswers][作业批改自动记录 {} 道错题，submissionId({}), userId({})]",
                    wrongIndices.size(), sub.getId(), sub.getUserId());
        }
    }

    /**
     * 根据标签名称查找已有标签或创建新标签，返回标签 ID 列表
     */
    private java.util.List<Long> resolveOrCreateTags(Long subjectId, java.util.List<String> tagNames,
                                                      java.util.Map<String, Long> existingMap) {
        java.util.List<Long> ids = new java.util.ArrayList<>();
        for (String name : tagNames) {
            name = name.trim();
            if (name.isEmpty() || name.length() > 100) continue;
            Long tagId = existingMap.get(name);
            if (tagId == null) {
                // 创建新的知识点标签
                AiKnowledgeTagDO newTag = new AiKnowledgeTagDO();
                newTag.setSubjectId(subjectId);
                newTag.setName(name);
                newTag.setDifficulty(3);
                newTag.setStatus(0);
                newTag.setSort(0);
                knowledgeTagMapper.insert(newTag);
                tagId = newTag.getId();
                existingMap.put(name, tagId);
                log.info("[resolveOrCreateTags][创建新知识点标签，subjectId({}), name({}), id({})]", subjectId, name, tagId);
            }
            ids.add(tagId);
        }
        return ids;
    }

    /** 从 JSON 片段中提取整数值 */
    private static int extractIntValue(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\"");
            if (start < 0) return -1;
            start = json.indexOf(":", start) + 1;
            while (start < json.length() && !Character.isDigit(json.charAt(start)) && json.charAt(start) != '-') start++;
            int end = start;
            while (end < json.length() && Character.isDigit(json.charAt(end))) end++;
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) { return -1; }
    }

    /** 从 JSON 片段中提取字符串数组，如 "knowledgeTags":["导数","积分"] */
    private static java.util.List<String> extractStringArray(String json, String key) {
        java.util.List<String> result = new java.util.ArrayList<>();
        try {
            int start = json.indexOf("\"" + key + "\"");
            if (start < 0) return result;
            int arrStart = json.indexOf("[", start);
            int arrEnd = json.indexOf("]", arrStart);
            if (arrStart < 0 || arrEnd < 0) return result;
            String arrContent = json.substring(arrStart + 1, arrEnd);
            // 提取每个引号内的字符串
            int i = 0;
            while (i < arrContent.length()) {
                int qStart = arrContent.indexOf("\"", i);
                if (qStart < 0) break;
                int qEnd = arrContent.indexOf("\"", qStart + 1);
                if (qEnd < 0) break;
                String val = arrContent.substring(qStart + 1, qEnd);
                if (!val.isBlank()) result.add(val.trim());
                i = qEnd + 1;
            }
        } catch (Exception e) { /* ignore */ }
        return result;
    }

    private static int parseIntFromJson(String json, String key) {
        try {
            String search = "\"" + key + "\"";
            int start = json.indexOf(search);
            if (start < 0) return 0;
            start = json.indexOf(":", start + search.length()) + 1;
            while (start < json.length() && Character.isWhitespace(json.charAt(start))) start++;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) { return 0; }
    }

    private static String extractStringFromJson(String json, String key) {
        try {
            String search = "\"" + key + "\"";
            int start = json.indexOf(search);
            if (start < 0) return "";
            start = json.indexOf("\"", start + search.length());
            if (start < 0) return "";
            start++;
            int end = start;
            boolean escaped = false;
            while (end < json.length()) {
                char c = json.charAt(end);
                if (escaped) { escaped = false; end++; continue; }
                if (c == '\\') { escaped = true; end++; continue; }
                if (c == '"') break;
                end++;
            }
            return json.substring(start, end).replace("\\\"", "\"").replace("\\n", "\n");
        } catch (Exception e) { return ""; }
    }
}
