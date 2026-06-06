package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkSubmissionDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiHomeworkMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiHomeworkSubmissionMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiQuestionBankMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
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
    @Resource private AiSystemConfigService configService;

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
            4. 总分保留整数

            输出 JSON 格式（严格只输出 JSON，不要有其他文字）：
            {\"totalScore\": 85, \"aiScore\": 85, \"gradeDetail\": \"每题详细批改内容...\", \"aiFeedback\": \"整体评价和建议...\"}
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
        ChatModel chatModel = modelService.getChatModel(model.getId());

        String userMessage = gradePrompt + "\n" + gradingInput;
        ChatResponse response = chatModel.call(new Prompt(userMessage));
        String resultText = response.getResult().getOutput().getText();

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
