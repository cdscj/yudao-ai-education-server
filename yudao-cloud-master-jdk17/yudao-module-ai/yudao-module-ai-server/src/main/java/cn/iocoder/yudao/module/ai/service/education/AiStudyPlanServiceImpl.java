package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.*;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.gateway.AiModelGateway;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service @Validated @Slf4j
public class AiStudyPlanServiceImpl implements AiStudyPlanService {

    @Resource private AiStudyPlanMapper mapper;
    @Resource private AiDailyReportService reportService;
    @Resource private AiLearningStatsDailyMapper statsMapper;
    @Resource private AiNotificationMapper notificationMapper;
    @Resource private AiModelService modelService;
    @Resource private AiModelGateway modelGateway;

    private static final String PLAN_PROMPT = """
            你是学习规划师。根据学生的每日学习报告和历史学习计划，生成今日学习计划。
            如果已有进行中的计划，在其基础上调整；如果没有，根据今日报告新建。
            输出JSON：{"title":"计划标题","goal":"今日目标","dailyTasks":[{"task":"具体任务","priority":1,"suggestedMinutes":30}],"encouragement":"鼓励语"}
            每天3-5个任务即可，任务要具体可执行。
            """;

    @Override public Long createPlan(AiStudyPlanDO p) {
        AiStudyPlanDO active = mapper.selectActiveByUserId(p.getUserId());
        if (active != null) throw exception(STUDY_PLAN_ALREADY_ACTIVE);
        mapper.insert(p); return p.getId();
    }

    @Override public void updatePlan(AiStudyPlanDO p) { validateExists(p.getId()); mapper.updateById(p); }

    @Override
    public void completePlan(Long id, Long userId) {
        AiStudyPlanDO p = validateExists(id);
        if (!p.getUserId().equals(userId)) throw exception(STUDY_PLAN_NOT_EXISTS);
        p.setStatus("COMPLETED"); p.setProgress(100); p.setCompletedDate(LocalDateTime.now());
        mapper.updateById(p);
    }

    @Override public AiStudyPlanDO getActivePlan(Long userId) { return mapper.selectActiveByUserId(userId); }
    @Override public AiStudyPlanDO getPlan(Long id) { return validateExists(id); }

    @Override
    public PageResult<AiStudyPlanDO> getPlanPage(Long userId, String planType, String status, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), userId, planType, status);
    }

    @Override
    public AiStudyPlanDO generateFromReport(Long userId) {
        // 1. 获取今日报告
        AiDailyReportDO report = reportService.getReportByDate(userId, LocalDate.now());
        String reportSummary = report != null ? report.getSummary() : "暂无今日报告，请先完成每日学习。";
        String weaknesses = report != null ? report.getWeaknesses() : "[]";

        // 2. 获取进行中的计划（如果有）
        AiStudyPlanDO existingPlan = mapper.selectActiveByUserId(userId);
        String existingPlanDesc = existingPlan != null
                ? "当前计划：" + existingPlan.getTitle() + "，目标：" + existingPlan.getGoal()
                  + "，进度：" + existingPlan.getProgress() + "%，内容：" + existingPlan.getAiGeneratedContent()
                : "暂无进行中的学习计划。";

        // 3. 构建 AI prompt
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            log.warn("[generateFromReport][userId={}] 无可用AI模型", userId);
            return null;
        }
        AiModelDO model = models.get(models.size() - 1);

        String userInput = String.format("""
                今日学习报告：%s
                薄弱环节：%s
                历史计划：%s
                """, reportSummary, weaknesses, existingPlanDesc);

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(PLAN_PROMPT));
        messages.add(new UserMessage(userInput));

        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(), model.getTemperature(), model.getMaxTokens());
        Prompt prompt = new Prompt(messages, options);

        try {
            String aiResult = modelGateway.chatSync(model.getId(), prompt);
            String jsonStr = aiResult.contains("```json") ? aiResult.substring(aiResult.indexOf("```json") + 7).split("```")[0].trim()
                    : aiResult.contains("{") ? aiResult.substring(aiResult.indexOf("{")).trim() : aiResult.trim();

            @SuppressWarnings("unchecked")
            Map<String, Object> planData = JsonUtils.parseObject(jsonStr, Map.class);

            String title = planData != null ? (String) planData.getOrDefault("title", "今日学习计划") : "今日学习计划";
            String goal = planData != null ? (String) planData.getOrDefault("goal", "") : "";
            Object tasks = planData != null ? planData.get("dailyTasks") : null;
            String encouragement = planData != null ? (String) planData.getOrDefault("encouragement", "") : "";

            // 4. 保存/更新计划
            if (existingPlan != null) {
                // 更新现有计划
                existingPlan.setTitle(title);
                existingPlan.setGoal(goal);
                existingPlan.setAiGeneratedContent(aiResult);
                existingPlan.setDailyPlans(tasks != null ? JsonUtils.toJsonString(tasks) : "[]");
                existingPlan.setSource("AI");
                mapper.updateById(existingPlan);
                // 发送通知
                sendPlanNotification(userId, title, encouragement);
                return existingPlan;
            } else {
                // 创建新计划
                AiStudyPlanDO newPlan = AiStudyPlanDO.builder()
                        .userId(userId).title(title).planType("DAILY").goal(goal)
                        .startDate(LocalDate.now()).endDate(LocalDate.now().plusDays(7))
                        .aiGeneratedContent(aiResult)
                        .dailyPlans(tasks != null ? JsonUtils.toJsonString(tasks) : "[]")
                        .status("ACTIVE").progress(0).source("AI").build();
                mapper.insert(newPlan);
                sendPlanNotification(userId, title, encouragement);
                return newPlan;
            }
        } catch (Exception e) {
            log.error("[generateFromReport][userId={}] AI生成计划失败", userId, e);
            return null;
        }
    }

    @Override
    public int batchGeneratePlans() {
        List<Long> activeUsers = getActiveUserIds();
        log.info("[batchGeneratePlans] 近7天活跃用户: {} 人", activeUsers.size());
        if (activeUsers.isEmpty()) return 0;

        int count = 0;
        for (Long userId : activeUsers) {
            try {
                AiStudyPlanDO plan = generateFromReport(userId);
                if (plan != null) count++;
            } catch (Exception e) {
                log.error("[batchGeneratePlans][userId={}] 失败", userId, e);
            }
        }
        log.info("[batchGeneratePlans] 完成, 成功 {}/{}", count, activeUsers.size());
        return count;
    }

    @Override
    public List<Long> getActiveUserIds() {
        // 近7天有学习统计记录的用户视为活跃；无记录则跳过，节省 AI token
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);
        List<AiLearningStatsDailyDO> stats = statsMapper.selectListSince(sevenDaysAgo);
        if (CollUtil.isEmpty(stats)) return List.of();
        return stats.stream()
                .map(AiLearningStatsDailyDO::getUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    private void sendPlanNotification(Long userId, String title, String encouragement) {
        try {
            AiNotificationDO n = AiNotificationDO.builder()
                    .userId(userId).notificationType("STUDY_PLAN")
                    .title("📋 " + title)
                    .content(encouragement != null && !encouragement.isEmpty()
                            ? encouragement : "AI 已为你生成今日学习计划，点击查看详情！")
                    .refType("STUDY_PLAN").build();
            notificationMapper.insert(n);
        } catch (Exception e) { log.warn("[sendPlanNotification][userId={}] 通知发送失败", userId, e); }
    }

    private AiStudyPlanDO validateExists(Long id) {
        AiStudyPlanDO p = mapper.selectById(id);
        if (p == null) throw exception(STUDY_PLAN_NOT_EXISTS);
        return p;
    }
}
