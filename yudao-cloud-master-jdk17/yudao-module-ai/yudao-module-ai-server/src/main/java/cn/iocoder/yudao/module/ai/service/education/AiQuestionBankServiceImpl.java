package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiSubjectCategoryDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiKnowledgeTagMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiQuestionBankMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.framework.ai.core.gateway.AiModelGateway;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class AiQuestionBankServiceImpl implements AiQuestionBankService {

    @Resource
    private AiQuestionBankMapper mapper;
    @Resource
    private AiModelService modelService;
    @Resource
    private AiModelGateway modelGateway;
    @Resource
    private AiKnowledgeTagMapper knowledgeTagMapper;
    @Resource
    private AiSubjectCategoryService subjectCategoryService;

    @Override
    public Long createQuestion(AiQuestionBankDO question) {
        mapper.insert(question);
        return question.getId();
    }

    @Override
    public void updateQuestion(AiQuestionBankDO question) {
        validateExists(question.getId());
        mapper.updateById(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        validateExists(id);
        mapper.deleteById(id);
    }

    @Override
    public AiQuestionBankDO getQuestion(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public PageResult<AiQuestionBankDO> getQuestionPage(Long subjectId, String questionType, Integer difficulty,
                                                         String keyword, Integer status, Integer pageNo, Integer pageSize) {
        // 自动扩展：如果学科有子类，同时查询子类题目
        Collection<Long> subjectIds = resolveSubjectIds(subjectId);
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), subjectIds, questionType, difficulty, keyword, status);
    }

    @Override
    public PageResult<AiQuestionBankDO> getQuestionPageBySubjectIds(Collection<Long> subjectIds, String questionType,
                                                                     Integer difficulty, String keyword, Integer status,
                                                                     Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), subjectIds, questionType, difficulty, keyword, status);
    }

    /**
     * 解析学科ID：如果是父学科，扩展为 [父ID + 所有子ID]
     */
    private Collection<Long> resolveSubjectIds(Long subjectId) {
        if (subjectId == null) return null;
        List<AiSubjectCategoryDO> children = subjectCategoryService.getEnabledList();
        List<Long> ids = new ArrayList<>();
        ids.add(subjectId);
        for (AiSubjectCategoryDO child : children) {
            if (subjectId.equals(child.getParentId())) {
                ids.add(child.getId());
            }
        }
        return ids;
    }

    @Override
    public List<AiQuestionBankDO> getQuestionListByIds(List<Long> ids) {
        return mapper.selectListByIds(ids);
    }

    private void validateExists(Long id) {
        if (mapper.selectById(id) == null) {
            throw exception(QUESTION_NOT_EXISTS);
        }
    }

    // ========== AI 生成题目 ==========

    private static final String DEFAULT_QUESTION_GENERATE_PROMPT = """
            你是一位专业的教育出题专家。请根据指定的学科、题型、难度、数量和关键词，生成高质量的题目。

            要求：
            1. 题目内容要准确、专业，符合指定学科的学术标准
            2. 选择题必须提供 4 个选项（A/B/C/D），标注正确答案
            3. 判断题答案必须是"正确"或"错误"
            4. 简答题需要提供清晰完整的参考答案
            5. 每题必须包含详细的解析，说明解题思路或知识点
            6. 难度 1-5（1=极易, 5=极难），严格按照指定难度生成
            7. 如果提供了关键词，题目应围绕该关键词展开

            严格按以下 JSON 格式输出（只输出 JSON 数组，不要其他内容）：
            [{"questionType":"CHOICE","title":"题目内容","options":{"A":"选项A","B":"选项B","C":"选项C","D":"选项D"},"answer":"A","analysis":"本题考察......","difficulty":3},
             {"questionType":"JUDGE","title":"题目内容","answer":"正确","analysis":"本题考察......","difficulty":2},
             {"questionType":"SHORT_ANSWER","title":"题目内容","answer":"参考答案","analysis":"本题考察......","difficulty":3}]
            """;

    @Override
    public int generateQuestionByAI(Long subjectId, String questionType, Integer difficulty, Integer count, String keyword, String knowledgeTagIds) {
        // 1. 获取启用的聊天模型
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            throw exception(MODEL_DEFAULT_NOT_EXISTS);
        }

        // 2. 解析知识点标签，加载标签名称
        String tagNames = "";
        if (StrUtil.isNotBlank(knowledgeTagIds)) {
            try {
                List<Long> tagIds = Arrays.stream(knowledgeTagIds.replace("[", "").replace("]", "").split(","))
                        .map(String::trim).filter(StrUtil::isNotBlank)
                        .map(Long::parseLong).collect(Collectors.toList());
                if (!CollUtil.isEmpty(tagIds)) {
                    List<AiKnowledgeTagDO> tags = knowledgeTagMapper.selectListByIds(tagIds);
                    if (!CollUtil.isEmpty(tags)) {
                        tagNames = tags.stream().map(AiKnowledgeTagDO::getName).collect(Collectors.joining("、"));
                    }
                }
            } catch (Exception e) {
                log.warn("[generateQuestionByAI][解析知识点标签失败，knowledgeTagIds({})]", knowledgeTagIds, e);
            }
        }

        // 3. 查学科名称
        String subjectName = "";
        try {
            AiSubjectCategoryDO subject = subjectCategoryService.getSubjectCategory(subjectId);
            subjectName = subject != null ? subject.getName() : ("学科#" + subjectId);
        } catch (Exception e) {
            subjectName = "学科#" + subjectId;
        }

        // 4. 构建提示词
        String typeDesc = Map.of("CHOICE", "选择题", "JUDGE", "判断题", "SHORT_ANSWER", "简答题")
                .getOrDefault(questionType, questionType);
        String userMsg = StrUtil.format("学科：{}，题型：{}，难度：{}，题目数量：{}",
                subjectName, typeDesc, difficulty != null ? difficulty : 3, count != null ? count : 5);
        if (StrUtil.isNotBlank(keyword)) {
            userMsg += "，关键词：" + keyword;
        }
        if (StrUtil.isNotBlank(tagNames)) {
            userMsg += "，涉及知识点：" + tagNames;
        }

        // 5. 调用 LLM（通过 Gateway 获得重试+熔断+fallback 保护）
        AiModelDO model = models.get(models.size() - 1);
        log.info("[generateQuestionByAI][开始调用模型 {}({})，subjectId({}), count({})]",
                model.getName(), model.getModel(), subjectId, count);
        String resultText;
        try {
            resultText = modelGateway.chatSync(model.getId(),
                    new Prompt(DEFAULT_QUESTION_GENERATE_PROMPT + "\n\n" + userMsg));
        } catch (Exception e) {
            log.error("[generateQuestionByAI][LLM 调用失败，model({})]", model.getModel(), e);
            throw exception(QUESTION_AI_GENERATE_FAIL);
        }

        // 5. 解析 AI 返回的 JSON 并保存到题库
        int savedCount = 0;
        try {
            String json = resultText.trim();
            // 处理 markdown 代码块包裹
            if (json.startsWith("```")) {
                json = json.substring(json.indexOf("\n") + 1);
                if (json.endsWith("```")) {
                    json = json.substring(0, json.lastIndexOf("```"));
                }
            }
            // 按 {"questionType" 分割每个题目 JSON 对象
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
                    q.setQuestionType(questionType);
                    q.setTitle(extractStr(part, "title"));
                    q.setAnswer(extractStr(part, "answer"));
                    q.setAnalysis(extractStr(part, "analysis"));
                    q.setDifficulty(extractInt(part, "difficulty"));
                    q.setStatus(0);
                    q.setSource("AI_GENERATED");
                    // 自动打上知识点标签
                    if (StrUtil.isNotBlank(knowledgeTagIds)) {
                        q.setKnowledgeTagIds(knowledgeTagIds);
                    }
                    // 处理 options JSON 对象
                    int optStart = part.indexOf("\"options\"");
                    if (optStart > 0) {
                        int braceStart = part.indexOf("{", optStart);
                        int braceEnd = part.indexOf("}", braceStart);
                        if (braceStart > 0 && braceEnd > braceStart) {
                            q.setOptions(part.substring(braceStart, braceEnd + 1));
                        }
                    }
                    mapper.insert(q);
                    savedCount++;
                } catch (Exception ex) {
                    log.warn("[generateQuestionByAI][解析单道题目失败，subjectId({})]", subjectId, ex);
                }
            }
        } catch (Exception e) {
            log.error("[generateQuestionByAI][AI 返回解析失败，subjectId({})]", subjectId, e);
            throw exception(QUESTION_AI_GENERATE_FAIL);
        }

        if (savedCount == 0) {
            throw exception(QUESTION_AI_GENERATE_FAIL);
        }
        return savedCount;
    }

    // ========== JSON 解析工具方法 ==========

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
            if (start < 0) return 3;
            start = json.indexOf(":", start) + 1;
            while (start < json.length() && !Character.isDigit(json.charAt(start)) && json.charAt(start) != '-') start++;
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
            return Integer.parseInt(json.substring(start, end));
        } catch (Exception e) { return 3; }
    }
}
