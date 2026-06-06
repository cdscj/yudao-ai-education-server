package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamRecordDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiExamMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiExamRecordMapper;
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
public class AiExamServiceImpl implements AiExamService {

    @Resource private AiExamMapper mapper;
    @Resource private AiExamRecordMapper recordMapper;
    @Resource private AiQuestionBankMapper questionBankMapper;
    @Resource private AiModelService modelService;
    @Resource private AiSystemConfigService configService;

    @Override public Long createExam(AiExamDO e) { mapper.insert(e); return e.getId(); }
    @Override public void updateExam(AiExamDO e) { validateExists(e.getId()); mapper.updateById(e); }
    @Override public void deleteExam(Long id) { validateExists(id); mapper.deleteById(id); }

    @Override
    public void publishExam(Long id) {
        AiExamDO e = validateExists(id);
        e.setPublishStatus("PUBLISHED");
        mapper.updateById(e);
    }

    @Override public AiExamDO getExam(Long id) { return validateExists(id); }

    @Override
    public PageResult<AiExamDO> getExamPage(Long subjectId, String publishStatus, String title, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), subjectId, publishStatus, title);
    }

    @Override
    public PageResult<AiExamDO> getPublishedList(Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), null, "PUBLISHED", null);
    }

    @Override
    @Transactional
    public Long startExam(Long examId, Long userId) {
        AiExamDO e = validateExists(examId);
        if (!"PUBLISHED".equals(e.getPublishStatus())) throw exception(EXAM_NOT_PUBLISHED);
        AiExamRecordDO record = new AiExamRecordDO();
        record.setExamId(examId).setUserId(userId).setStartTime(LocalDateTime.now()).setStatus("IN_PROGRESS");
        recordMapper.insert(record);
        return record.getId();
    }

    @Override
    @Transactional
    public void submitExam(Long examId, Long userId, String answers, Integer durationSeconds) {
        validateExists(examId);
        AiExamRecordDO record = recordMapper.selectListByUserId(userId).stream()
                .filter(r -> r.getExamId().equals(examId) && "IN_PROGRESS".equals(r.getStatus()))
                .findFirst().orElse(null);
        if (record == null) throw exception(EXAM_RECORD_NOT_EXISTS);
        record.setAnswers(answers).setSubmitTime(LocalDateTime.now()).setDurationSeconds(durationSeconds).setStatus("SUBMITTED");
        recordMapper.updateById(record);
    }

    @Override
    public AiExamRecordDO getRecord(Long examId, Long userId) {
        return recordMapper.selectListByUserId(userId).stream()
                .filter(r -> r.getExamId().equals(examId)).findFirst().orElse(null);
    }

    @Override
    public PageResult<AiExamRecordDO> getRecordPage(Long userId, Integer pageNo, Integer pageSize) {
        return recordMapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), null, userId, null);
    }

    private AiExamDO validateExists(Long id) {
        AiExamDO e = mapper.selectById(id);
        if (e == null) throw exception(EXAM_NOT_EXISTS);
        return e;
    }

    private static final String DEFAULT_EXAM_GENERATE_PROMPT = """
            你是一位专业的考试出题专家。请根据指定学科、难度和数量，生成考试题目。

            要求：
            1. 题目类型包含选择题、判断题和简答题，比例约 6:2:2
            2. 选择题 4 个选项（A/B/C/D），标注正确答案
            3. 判断题标注正确答案（正确/错误）
            4. 每题需有解析
            5. 难度 1-5（1=极易, 5=极难）

            严格按以下 JSON 格式输出（只输出 JSON，不要其他内容）：
            [{"questionType":"CHOICE","title":"题目内容","options":{"A":"选项A","B":"选项B","C":"选项C","D":"选项D"},"answer":"A","analysis":"解析","difficulty":3},
             {"questionType":"JUDGE","title":"题目内容","answer":"正确","analysis":"解析","difficulty":2},
             {"questionType":"SHORT_ANSWER","title":"题目内容","answer":"参考答案","analysis":"解析","difficulty":3}]
            """;

    private static final String DEFAULT_EXAM_ANALYSIS_PROMPT = """
            你是一位考试分析专家。请根据考生的答题情况和正确答案，进行错题分析和学习诊断。

            请按以下 JSON 格式输出（只输出 JSON，不要其他内容）：
            {"totalScore":85,"aiScore":85,"aiFeedback":"整体评价...","gradeDetail":"逐题分析...","weakPoints":["薄弱点1","薄弱点2"],"suggestions":"针对性学习建议"}
            """;

    @Override
    public void generateExamByAI(Long examId, Long subjectId, Integer difficulty, Integer questionCount) {
        AiExamDO exam = validateExists(examId);
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) throw exception(MODEL_DEFAULT_NOT_EXISTS);

        String genPrompt = configService.getConfigValue("edu.exam.generate.prompt", DEFAULT_EXAM_GENERATE_PROMPT);
        String userMsg = StrUtil.format("学科ID：{}，难度：{}，题目数量：{}",
                subjectId, difficulty != null ? difficulty : 3, questionCount != null ? questionCount : 10);

        AiModelDO model = models.get(models.size() - 1);
        ChatModel chatModel = modelService.getChatModel(model.getId());
        ChatResponse response = chatModel.call(new Prompt(genPrompt + "\n\n" + userMsg));
        String resultText = response.getResult().getOutput().getText();

        // 解析 AI 生成的题目并保存到题库
        try {
            String json = resultText.trim();
            if (json.startsWith("```")) {
                json = json.substring(json.indexOf("\n") + 1);
                if (json.endsWith("```")) json = json.substring(0, json.lastIndexOf("```"));
            }
            List<String> savedIds = new java.util.ArrayList<>();
            // 简单 JSON 解析：按 {"questionType" 分割
            String[] parts = json.split("\\{\"questionType\"");
            for (int i = 1; i < parts.length; i++) {
                String part = "{\"questionType\"" + parts[i];
                if (part.endsWith(",")) part = part.substring(0, part.length() - 1);
                if (!part.endsWith("}")) {
                    int lastBrace = part.lastIndexOf('}');
                    if (lastBrace > 0) part = part.substring(0, lastBrace + 1);
                }
                try {
                    AiQuestionBankDO q = new AiQuestionBankDO();
                    q.setSubjectId(subjectId);
                    q.setQuestionType(extractStr(part, "questionType"));
                    q.setTitle(extractStr(part, "title"));
                    q.setAnswer(extractStr(part, "answer"));
                    q.setAnalysis(extractStr(part, "analysis"));
                    q.setDifficulty(extractInt(part, "difficulty"));
                    q.setStatus(0);
                    // 处理 options JSON 对象
                    String opts = part.contains("\"options\"") ? part : null;
                    if (opts != null) {
                        int optStart = part.indexOf("\"options\"");
                        int braceStart = part.indexOf("{", optStart);
                        int braceEnd = part.indexOf("}", braceStart);
                        if (braceStart > 0 && braceEnd > braceStart) {
                            q.setOptions(part.substring(braceStart, braceEnd + 1));
                        }
                    }
                    questionBankMapper.insert(q);
                    savedIds.add(String.valueOf(q.getId()));
                } catch (Exception ex) {
                    log.warn("[generateExamByAI][解析题目失败，examId({})]", examId, ex);
                }
            }
            if (!savedIds.isEmpty()) {
                exam.setQuestionIds(String.join(",", savedIds));
                mapper.updateById(exam);
            }
        } catch (Exception e) {
            log.warn("[generateExamByAI][AI 返回格式解析失败，examId({})]", examId, e);
            exam.setDescription(resultText);
            mapper.updateById(exam);
        }
    }

    @Override
    public void analyzeExamRecord(Long recordId) {
        AiExamRecordDO record = recordMapper.selectById(recordId);
        if (record == null) throw exception(EXAM_RECORD_NOT_EXISTS);
        AiExamDO exam = validateExists(record.getExamId());

        // 查询题库中的题目获取正确答案
        List<AiQuestionBankDO> questions = List.of();
        if (exam.getQuestionIds() != null && !exam.getQuestionIds().isBlank()) {
            List<Long> qIds = Arrays.stream(exam.getQuestionIds().split(","))
                    .map(String::trim).filter(StrUtil::isNotBlank)
                    .map(Long::parseLong).collect(Collectors.toList());
            if (!CollUtil.isEmpty(qIds)) questions = questionBankMapper.selectListByIds(qIds);
        }

        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) throw exception(MODEL_DEFAULT_NOT_EXISTS);

        String analysisPrompt = configService.getConfigValue("edu.exam.analysis.prompt", DEFAULT_EXAM_ANALYSIS_PROMPT);

        StringBuilder input = new StringBuilder();
        input.append("考试：" + exam.getTitle()).append("\n");
        input.append("总分：" + exam.getTotalScore()).append("\n\n");
        for (int i = 0; i < questions.size(); i++) {
            AiQuestionBankDO q = questions.get(i);
            input.append("题").append(i + 1).append(") [").append(q.getQuestionType()).append("] ");
            input.append(q.getTitle()).append("\n");
            input.append("  参考答案：" + q.getAnswer()).append("\n\n");
        }
        if (record.getAnswers() != null) {
            String[] studentAnswers = record.getAnswers().split("\n");
            input.append("学生答案：\n");
            for (int i = 0; i < studentAnswers.length; i++) {
                input.append("题").append(i + 1).append(") ").append(studentAnswers[i]).append("\n");
            }
        }

        AiModelDO model = models.get(models.size() - 1);
        ChatModel chatModel = modelService.getChatModel(model.getId());
        ChatResponse response = chatModel.call(new Prompt(analysisPrompt + "\n\n" + input));
        String resultText = response.getResult().getOutput().getText();

        // 解析结果
        try {
            String json = resultText.trim();
            if (json.contains("```json")) {
                json = json.substring(json.indexOf("```json") + 7);
                if (json.contains("```")) json = json.substring(0, json.lastIndexOf("```"));
            }
            int totalScore = parseIntFromJson(json, "totalScore");
            int aiScore = parseIntFromJson(json, "aiScore");
            record.setTotalScore(totalScore > 0 ? totalScore : (aiScore > 0 ? aiScore : 0));
            record.setAiScore(aiScore > 0 ? aiScore : totalScore);
            record.setAiFeedback(extractStringFromJson(json, "aiFeedback"));
            record.setGradeDetail(extractStringFromJson(json, "gradeDetail"));
        } catch (Exception e) {
            record.setGradeDetail(resultText);
            record.setAiFeedback("");
            record.setTotalScore(0);
        }
        recordMapper.updateById(record);
    }

    private static String extractStr(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\"");
            if (start < 0) return "";
            start = json.indexOf("\"", start + key.length() + 2) + 1;
            int end = start;
            boolean esc = false;
            while (end < json.length()) {
                if (esc) { esc = false; end++; continue; }
                if (json.charAt(end) == '\\') { esc = true; end++; continue; }
                if (json.charAt(end) == '"') break;
                end++;
            }
            return json.substring(start, end).replace("\\\"", "\"").replace("\\n", "\n");
        } catch (Exception e) { return ""; }
    }

    private static int extractInt(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\"");
            if (start < 0) return 1;
            start = json.indexOf(":", start) + 1;
            while (start < json.length() && !Character.isDigit(json.charAt(start)) && json.charAt(start) != '-') start++;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) { return 1; }
    }

    private static int parseIntFromJson(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\"");
            if (start < 0) return 0;
            start = json.indexOf(":", start) + 1;
            while (start < json.length() && !Character.isDigit(json.charAt(start)) && json.charAt(start) != '-') start++;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) { return 0; }
    }

    private static String extractStringFromJson(String json, String key) {
        try {
            int start = json.indexOf("\"" + key + "\"");
            if (start < 0) return "";
            start = json.indexOf("\"", start + key.length() + 2) + 1;
            int end = start;
            boolean esc = false;
            while (end < json.length()) {
                if (esc) { esc = false; end++; continue; }
                if (json.charAt(end) == '\\') { esc = true; end++; continue; }
                if (json.charAt(end) == '"') break;
                end++;
            }
            return json.substring(start, end).replace("\\\"", "\"").replace("\\n", "\n");
        } catch (Exception e) { return ""; }
    }
}
