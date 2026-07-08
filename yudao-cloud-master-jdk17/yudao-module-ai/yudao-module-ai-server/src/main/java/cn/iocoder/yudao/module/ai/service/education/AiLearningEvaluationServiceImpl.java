package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.EvaluationPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.*;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.framework.ai.core.gateway.AiModelGateway;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class AiLearningEvaluationServiceImpl implements AiLearningEvaluationService {

    @Resource private AiLearningEvaluationMapper evaluationMapper;
    @Resource private AiLearningResourceMapper learningResourceMapper;
    @Resource private AiStudentProfileService studentProfileService;
    @Resource private AiWrongAnswerBookService wrongAnswerBookService;
    @Resource private AiHomeworkSubmissionMapper homeworkSubmissionMapper;
    @Resource private AiHomeworkMapper homeworkMapper;
    @Resource private AiExamRecordMapper examRecordMapper;
    @Resource private AiExamMapper examMapper;
    @Resource private AiModelService modelService;
    @Resource private AiModelGateway modelGateway;
    @Resource private AiSystemConfigService configService;

    private static final String DEFAULT_EVALUATION_PROMPT = """
            你是一位专业的学习效果评估专家。请根据提供的多维学习数据，进行全面、客观的学习评估。

            ## 数据来源说明
            你将收到以下6个维度的学习数据：
            1. 学生画像 - 学生背景、学习目标、偏好
            2. 错题本统计 - 薄弱知识点排名、错误次数
            3. 作业成绩 - 历次作业得分、批改反馈
            4. 考试记录 - 模拟考试得分、答题情况
            5. 学习资源 - 已完成的学习资源和进度
            6. 学习计划 - 当前学习计划和完成情况

            ## 评估要求
            请综合分析上述数据，输出以下JSON格式评估报告（严格只输出JSON，不要其他文字）：

            ```json
            {
              "dimensions": [
                {"dimension": "知识掌握度", "score": 82, "maxScore": 100, "evaluation": "根据作业和考试成绩...", "suggestion": "建议..."},
                {"dimension": "学习效率", "score": 70, "maxScore": 100, "evaluation": "根据完成资源和时间...", "suggestion": "建议..."},
                {"dimension": "薄弱环节", "score": 0, "maxScore": 0, "evaluation": "薄弱知识点：导数（错12次）、积分（错8次）", "suggestion": "针对薄弱点加强练习"},
                {"dimension": "进步趋势", "score": 0, "maxScore": 0, "evaluation": "近一周成绩呈上升趋势", "suggestion": "保持当前节奏"},
                {"dimension": "学习习惯", "score": 75, "maxScore": 100, "evaluation": "根据提交频次和完成度...", "suggestion": "建议..."}
              ],
              "overallScore": 76,
              "overallMaxScore": 100,
              "overallEvaluation": "综合来看，你的学习表现...",
              "overallSuggestion": "接下来建议你重点关注导数与微分的练习...",
              "weakPoints": ["导数与微分", "定积分计算"],
              "strongPoints": ["Python基础语法", "数据结构"]
            }
            ```
            """;

    @Override
    public Flux<CommonResult<String>> generateEvaluation(Long userId) {
        // 1. 获取启用的模型
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            log.error("[generateEvaluation][userId({}) 无可用模型]", userId);
            return Flux.just(error(MODEL_DEFAULT_NOT_EXISTS));
        }

        // 2. 收集多维学习数据
        StringBuilder data = new StringBuilder();
        data.append("## 学生综合学习数据报告\n\n");

        // 2a. 学生画像
        AiStudentProfileDO profile = studentProfileService.getProfileByUserId(userId);
        data.append("### 1. 学生画像\n");
        data.append(profile != null ? profile.getProfileJson() : "暂无画像数据").append("\n\n");

        // 2b. 错题本统计 + 薄弱知识点
        List<AiWrongAnswerBookService.WeakPointVO> weakPoints =
                wrongAnswerBookService.getWeakPointAnalysis(userId, null);
        data.append("### 2. 错题本与薄弱知识点\n");
        data.append("总错题数：").append(wrongAnswerBookService.getStats(userId).get("totalCount")).append("\n");
        if (!CollUtil.isEmpty(weakPoints)) {
            data.append("薄弱知识点排名（按错误次数降序）：\n");
            for (int i = 0; i < weakPoints.size(); i++) {
                AiWrongAnswerBookService.WeakPointVO wp = weakPoints.get(i);
                data.append(i + 1).append(". ").append(wp.tagName())
                        .append(" - 错题数：").append(wp.wrongCount())
                        .append("，掌握度：").append(wp.masteryLevel()).append("/5\n");
            }
        } else {
            data.append("暂无错题记录（或题目未标注知识点标签）\n");
        }
        data.append("\n");

        // 2c. 作业成绩
        data.append("### 3. 作业成绩\n");
        PageResult<AiHomeworkSubmissionDO> homeworkPage = homeworkSubmissionMapper.selectPage(
                new PageParam().setPageNo(1).setPageSize(20),
                null, userId, "GRADED");
        if (homeworkPage != null && !CollUtil.isEmpty(homeworkPage.getList())) {
            for (AiHomeworkSubmissionDO sub : homeworkPage.getList()) {
                AiHomeworkDO hw = homeworkMapper.selectById(sub.getHomeworkId());
                String title = hw != null ? hw.getTitle() : "未知作业";
                data.append("- ").append(title)
                        .append("：得分 ").append(sub.getTotalScore() != null ? sub.getTotalScore() : "?")
                        .append("，AI 评价：").append(StrUtil.sub(sub.getAiFeedback(), 0, 100)).append("\n");
            }
        } else {
            data.append("暂无批改完成的作业记录\n");
        }
        data.append("\n");

        // 2d. 考试记录
        data.append("### 4. 考试记录\n");
        PageResult<AiExamRecordDO> examPage = examRecordMapper.selectPage(
                new PageParam().setPageNo(1).setPageSize(20),
                null, userId, null);
        if (examPage != null && !CollUtil.isEmpty(examPage.getList())) {
            for (AiExamRecordDO record : examPage.getList()) {
                AiExamDO exam = examMapper.selectById(record.getExamId());
                String title = exam != null ? exam.getTitle() : "未知考试";
                data.append("- ").append(title)
                        .append("：得分 ").append(record.getTotalScore() != null ? record.getTotalScore() : "?")
                        .append("，状态：").append(record.getStatus()).append("\n");
            }
        } else {
            data.append("暂无考试记录\n");
        }
        data.append("\n");

        // 2e. 学习资源
        data.append("### 5. 学习资源\n");
        List<AiLearningResourceDO> resources = learningResourceMapper.selectListByUserId(userId);
        if (!CollUtil.isEmpty(resources)) {
            long completed = resources.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count();
            data.append("总资源数：").append(resources.size())
                    .append("，已完成：").append(completed)
                    .append("，完成率：").append(resources.size() > 0 ? completed * 100 / resources.size() : 0).append("%\n");
            for (AiLearningResourceDO r : resources) {
                data.append("- ").append(r.getTitle())
                        .append(" [").append(r.getResourceType()).append("]")
                        .append(" 状态：").append(r.getStatus()).append("\n");
            }
        } else {
            data.append("暂无学习资源记录\n");
        }
        data.append("\n");

        // 2f. 学习计划（如有）
        data.append("### 6. 学习计划\n");
        data.append("请根据以上数据综合判断学习状态。\n");

        // 3. 构建 AI 消息
        List<Message> messages = new ArrayList<>();
        String evalPrompt = configService.getConfigValue("edu.evaluation.prompt", DEFAULT_EVALUATION_PROMPT);
        messages.add(new SystemMessage(evalPrompt));
        messages.add(new UserMessage(data.toString()));

        // 4. 流式调用 AI（通过 Gateway 获得重试+熔断+fallback 保护）
        Long profileId = profile != null ? profile.getId() : null;
        Long tenantId = cn.iocoder.yudao.framework.tenant.core.context.TenantContextHolder.getTenantId();
        AiModelDO selectedModel = models.get(models.size() - 1);
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(selectedModel.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, selectedModel.getModel(),
                selectedModel.getTemperature(), selectedModel.getMaxTokens());
        Prompt prompt = new Prompt(messages, options);

        StringBuffer contentBuffer = new StringBuffer();
        return modelGateway.chatStream(selectedModel.getId(), prompt)
                .map(text -> {
                    contentBuffer.append(text);
                    return success(text);
                }).doOnComplete(() -> {
                    TenantUtils.executeIgnore(() -> saveEvaluation(userId, profileId, tenantId, contentBuffer.toString()));
                }).doOnError(throwable -> {
                    log.error("[generateEvaluation][userId({}) 生成异常]", userId, throwable);
                });
    }

    /**
     * 解析 AI 返回的评估 JSON 并保存到数据库
     */
    private void saveEvaluation(Long userId, Long profileId, Long tenantId, String aiResponse) {
        try {
            String json = aiResponse.trim();
            // 处理 markdown 代码块
            if (json.startsWith("```")) {
                int nl = json.indexOf("\n");
                json = nl > 0 ? json.substring(nl + 1) : json.substring(3);
                if (json.endsWith("```")) {
                    json = json.substring(0, json.lastIndexOf("```"));
                }
            }
            json = json.trim();

            // 提取顶层字段
            String overallEval = extractJsonStr(json, "overallEvaluation");
            String overallSugg = extractJsonStr(json, "overallSuggestion");
            int overallScore = extractJsonInt(json, "overallScore");
            int overallMax = extractJsonInt(json, "overallMaxScore");
            if (overallMax <= 0) overallMax = 100;
            String weakPoints = extractJsonArray(json, "weakPoints");
            String strongPoints = extractJsonArray(json, "strongPoints");

            // 保存综合评估
            AiLearningEvaluationDO mainEval = new AiLearningEvaluationDO();
            mainEval.setTenantId(tenantId);
            mainEval.setUserId(userId).setProfileId(profileId)
                    .setEvaluationType("COMPREHENSIVE")
                    .setDimension("综合评估")
                    .setScore(overallScore > 0 ? overallScore : 0)
                    .setMaxScore(overallMax)
                    .setEvaluation(StrUtil.blankToDefault(overallEval,
                            StrUtil.sub(aiResponse, 0, 500)))
                    .setSuggestion(StrUtil.blankToDefault(overallSugg, ""))
                    .setEvaluationJson(aiResponse);
            evaluationMapper.insert(mainEval);

            // 解析 dimensions 数组，每个维度存一条记录
            String dimsJson = extractJsonArrayStr(json, "dimensions");
            if (StrUtil.isNotBlank(dimsJson)) {
                String[] dimItems = dimsJson.split("\\},\\s*\\{");
                for (String item : dimItems) {
                    item = item.trim();
                    if (!item.startsWith("{")) item = "{" + item;
                    if (!item.endsWith("}")) item = item + "}";
                    try {
                        String dimName = extractJsonStr(item, "dimension");
                        int dimScore = extractJsonInt(item, "score");
                        int dimMax = extractJsonInt(item, "maxScore");
                        String dimEval = extractJsonStr(item, "evaluation");
                        String dimSugg = extractJsonStr(item, "suggestion");

                        AiLearningEvaluationDO dimEvalDO = new AiLearningEvaluationDO();
                        dimEvalDO.setTenantId(tenantId);
                        dimEvalDO.setUserId(userId).setProfileId(profileId)
                                .setEvaluationType("DIMENSION")
                                .setDimension(StrUtil.blankToDefault(dimName, "未命名维度"))
                                .setScore(dimScore > 0 ? dimScore : 0)
                                .setMaxScore(dimMax > 0 ? dimMax : 100)
                                .setEvaluation(StrUtil.blankToDefault(dimEval, ""))
                                .setSuggestion(StrUtil.blankToDefault(dimSugg, ""))
                                .setEvaluationJson(item);
                        evaluationMapper.insert(dimEvalDO);
                    } catch (Exception e) {
                        log.warn("[saveEvaluation][解析维度失败，userId({})]", userId, e);
                    }
                }
            }

            log.info("[saveEvaluation][userId({}) 评估保存成功，dimensions({})]", userId,
                    dimsJson != null ? dimsJson.split("\\},\\s*\\{").length : 0);
        } catch (Exception e) {
            // 解析失败时至少保存原始内容
            log.warn("[saveEvaluation][解析评估JSON失败，保存原始内容，userId({})]", userId, e);
            AiLearningEvaluationDO fallback = new AiLearningEvaluationDO();
            fallback.setTenantId(tenantId);
            fallback.setUserId(userId).setProfileId(profileId)
                    .setEvaluationType("COMPREHENSIVE")
                    .setDimension("综合评估")
                    .setScore(0).setMaxScore(100)
                    .setEvaluation(StrUtil.sub(aiResponse, 0, 500))
                    .setEvaluationJson(aiResponse);
            evaluationMapper.insert(fallback);
        }
    }

    // ========== JSON 解析工具方法 ==========

    private static String extractJsonStr(String json, String key) {
        try {
            String search = "\"" + key + "\"";
            int start = json.indexOf(search);
            if (start < 0) return "";
            int colon = json.indexOf(":", start + search.length());
            if (colon < 0) return "";
            // 跳过冒号后的空白
            int valStart = colon + 1;
            while (valStart < json.length() && Character.isWhitespace(json.charAt(valStart))) valStart++;
            if (valStart >= json.length()) return "";
            if (json.charAt(valStart) == '"') {
                // 字符串值
                valStart++; // 跳过开始的引号
                int end = valStart;
                boolean esc = false;
                while (end < json.length()) {
                    if (esc) { esc = false; end++; continue; }
                    if (json.charAt(end) == '\\') { esc = true; end++; continue; }
                    if (json.charAt(end) == '"') break;
                    end++;
                }
                return json.substring(valStart, end).replace("\\\"", "\"").replace("\\n", "\n");
            } else if (json.charAt(valStart) == '[') {
                // 数组值 - 返回完整数组
                int end = valStart + 1;
                int depth = 1;
                boolean inStr = false, esc = false;
                while (end < json.length() && depth > 0) {
                    char c = json.charAt(end);
                    if (esc) { esc = false; end++; continue; }
                    if (c == '\\' && inStr) { esc = true; end++; continue; }
                    if (c == '"') { inStr = !inStr; end++; continue; }
                    if (!inStr) {
                        if (c == '[') depth++;
                        else if (c == ']') depth--;
                    }
                    end++;
                }
                return json.substring(valStart, end);
            }
            return "";
        } catch (Exception e) { return ""; }
    }

    private static int extractJsonInt(String json, String key) {
        try {
            String search = "\"" + key + "\"";
            int start = json.indexOf(search);
            if (start < 0) return 0;
            int colon = json.indexOf(":", start + search.length());
            if (colon < 0) return 0;
            int valStart = colon + 1;
            while (valStart < json.length() && Character.isWhitespace(json.charAt(valStart))) valStart++;
            int end = valStart;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
            return end > valStart ? Integer.parseInt(json.substring(valStart, end)) : 0;
        } catch (Exception e) { return 0; }
    }

    /** 从 JSON 中提取维度数组的原始 JSON 字符串 */
    private static String extractJsonArrayStr(String json, String key) {
        try {
            String search = "\"" + key + "\"";
            int start = json.indexOf(search);
            if (start < 0) return "";
            int colon = json.indexOf(":", start + search.length());
            if (colon < 0) return "";
            int arrStart = colon + 1;
            while (arrStart < json.length() && Character.isWhitespace(json.charAt(arrStart))) arrStart++;
            if (arrStart >= json.length() || json.charAt(arrStart) != '[') return "";
            int depth = 1, i = arrStart + 1;
            boolean inStr = false, esc = false;
            while (i < json.length() && depth > 0) {
                char c = json.charAt(i);
                if (esc) { esc = false; i++; continue; }
                if (c == '\\' && inStr) { esc = true; i++; continue; }
                if (c == '"') { inStr = !inStr; i++; continue; }
                if (!inStr) {
                    if (c == '[') depth++;
                    else if (c == ']') depth--;
                }
                i++;
            }
            return json.substring(arrStart + 1, i - 1); // 去掉外层的 [ ]
        } catch (Exception e) { return ""; }
    }

    /** 从 JSON 中提取字符串数组的内容，如 ["a","b"] → a,b */
    private static String extractJsonArray(String json, String key) {
        String arr = extractJsonStr(json, key);
        if (StrUtil.isBlank(arr) || !arr.startsWith("[")) return "";
        // 提取数组中的字符串元素
        StringBuilder sb = new StringBuilder();
        String[] items = arr.split("\\\",\\s*\\\"");
        for (String item : items) {
            String cleaned = item.replace("[", "").replace("]", "").replace("\"", "").trim();
            if (!cleaned.isEmpty()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(cleaned);
            }
        }
        return sb.toString();
    }

    @Override
    public PageResult<AiLearningEvaluationDO> getEvaluationPage(EvaluationPageReqVO reqVO) {
        return evaluationMapper.selectPage(reqVO);
    }
}
