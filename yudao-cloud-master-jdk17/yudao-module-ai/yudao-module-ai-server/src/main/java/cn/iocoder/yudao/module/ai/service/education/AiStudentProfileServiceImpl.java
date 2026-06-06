package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.StudentProfileChatReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudentProfileDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiStudentProfileMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class AiStudentProfileServiceImpl implements AiStudentProfileService {

    @Resource
    private AiStudentProfileMapper studentProfileMapper;
    @Resource
    private AiModelService modelService;

    private static final int MAX_HISTORY_TURNS = 20;

    private static final String PROFILE_SYSTEM_PROMPT = """
            你是一位智能的学生画像构建助手。你需要通过对话了解学生信息，并构建6个维度的学生画像。
            画像维度：1.基本信息(专业、年级) 2.学习目标 3.知识水平 4.学习偏好 5.薄弱环节 6.优势能力

            请根据与学生对话逐步提取信息。每次用户发言后，如果你已经收集到足够信息，请输出JSON格式的画像数据，
            否则继续提问收集信息。

            输出JSON格式：
            `json
            {
              "major": "专业",
              "grade": "年级",
              "learningGoals": "学习目标",
              "knowledgeLevel": "知识水平描述",
              "learningPreferences": "学习偏好",
              "weakPoints": "薄弱环节",
              "strongPoints": "优势能力",
              "studyHistory": "学习经历",
              "learningSpeed": 3,
              "preferredResourceTypes": "偏好资源类型",
              "studyTimePreference": "学习时间偏好"
            }
            `

            注意：learningSpeed为1-5的整数。只有当信息足够完整时才输出JSON，否则继续提问。
            """;

    @Override
    public AiStudentProfileDO getProfileByUserId(Long userId) {
        return studentProfileMapper.selectByUserId(userId);
    }

    @Override
    public Flux<CommonResult<String>> chatBuildProfile(StudentProfileChatReqVO chatReqVO, Long userId) {
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            log.error("[chatBuildProfile][userId({}) 无可用模型]", userId);
            return Flux.just(error(MODEL_DEFAULT_NOT_EXISTS));
        }

        // 1. 加载现有画像 + 对话历史（用数组 holder 满足 lambda effectively final 要求）
        final AiStudentProfileDO[] profileRef = new AiStudentProfileDO[1];
        profileRef[0] = studentProfileMapper.selectByUserId(userId);
        if (profileRef[0] == null) {
            profileRef[0] = new AiStudentProfileDO();
            profileRef[0].setUserId(userId);
            profileRef[0].setProfileJson("{}");
            profileRef[0].setConversationHistory("[]");
            studentProfileMapper.insert(profileRef[0]);
        }
        String historyJson = profileRef[0].getConversationHistory();

        // 2. 追加本轮用户消息到历史 + 同步保存（确保对话不丢失）
        List<Map<String, Object>> historyList = parseHistoryList(historyJson);
        Map<String, Object> userTurn = new HashMap<>();
        userTurn.put("role", "user");
        userTurn.put("content", chatReqVO.getMessage());
        historyList.add(userTurn);
        profileRef[0].setConversationHistory(JSONUtil.toJsonStr(historyList));
        studentProfileMapper.updateById(profileRef[0]);

        // 3. 组装 Prompt（system + 历史 + 当前消息）
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(PROFILE_SYSTEM_PROMPT));
        messages.addAll(buildHistoryMessages(historyJson));
        messages.add(new UserMessage(chatReqVO.getMessage()));

        // 4. 流式推理 + 多模型 fallback
        Flux<CommonResult<String>> result = null;
        for (int i = models.size() - 1; i >= 0; i--) {
            AiModelDO model = models.get(i);
            ChatModel chatModel;
            try {
                chatModel = modelService.getChatModel(model.getId());
            } catch (Exception e) {
                log.error("[chatBuildProfile][userId({}) 模型({}) 创建ChatModel失败]", userId, model.getId(), e);
                continue;
            }
            AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
            ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                    model.getTemperature(), model.getMaxTokens());
            Prompt prompt = new Prompt(messages, options);

            StringBuffer contentBuffer = new StringBuffer();
            Flux<CommonResult<String>> stream = chatModel.stream(prompt).map(chunk -> {
                String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : "";
                contentBuffer.append(newContent);
                return success(newContent);
            }).doOnError(throwable -> {
                log.error("[chatBuildProfile][userId({}) 模型({}) 流式输出失败]", userId, model.getId(), throwable);
            }).doOnComplete(() -> {
                TenantUtils.executeIgnore(() -> {
                    String fullContent = contentBuffer.toString();
                    // 保存 assistant 消息到历史
                    Map<String, Object> assistantTurn = new HashMap<>();
                    assistantTurn.put("role", "assistant");
                    assistantTurn.put("content", fullContent);
                    historyList.add(assistantTurn);
                    while (historyList.size() > MAX_HISTORY_TURNS * 2) {
                        historyList.remove(0);
                    }
                    String updatedHistoryJson = JSONUtil.toJsonStr(historyList);
                    // 提取画像 JSON（可能为空）
                    String jsonStr = extractJsonFromContent(fullContent);
                    if (jsonStr != null) {
                        profileRef[0].setProfileJson(jsonStr);
                    }
                    profileRef[0].setConversationHistory(updatedHistoryJson);
                    studentProfileMapper.updateById(profileRef[0]);
                });
            });

            if (result == null) {
                result = stream;
            } else {
                Flux<CommonResult<String>> current = stream;
                Flux<CommonResult<String>> next = result;
                result = current.onErrorResume(e -> {
                    log.error("[chatBuildProfile][userId({}) 模型({}) 失败，切换到下一个: {}]", userId, model.getId(), e.getMessage());
                    return next;
                });
            }
        }
        return result != null ? result.onErrorResume(error -> {
            log.error("[chatBuildProfile][userId({}) 所有模型均失败]", userId, error);
            return Flux.just(error(EDUCATION_STREAM_ERROR));
        }) : Flux.just(error(EDUCATION_STREAM_ERROR));
    }

    @Override
    public AiStudentProfileDO updateProfile(Long id, AiStudentProfileDO profile) {
        studentProfileMapper.updateById(profile);
        return studentProfileMapper.selectById(id);
    }

    /**
     * 将对话历史 JSON 数组解析为 Spring AI Message 列表（只取最近 MAX_HISTORY_TURNS 轮）
     */
    private List<Message> buildHistoryMessages(String historyJson) {
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isBlank(historyJson)) return messages;
        try {
            JSONArray arr = JSONUtil.parseArray(historyJson);
            int start = Math.max(0, arr.size() - MAX_HISTORY_TURNS * 2);
            for (int i = start; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String role = obj.getStr("role");
                String content = obj.getStr("content");
                if ("user".equals(role)) {
                    messages.add(new UserMessage(content));
                } else if ("assistant".equals(role)) {
                    messages.add(new AssistantMessage(content));
                }
            }
        } catch (Exception e) {
            log.warn("[buildHistoryMessages][解析历史消息失败, historyJson={}]", historyJson, e);
        }
        return messages;
    }

    /**
     * 将对话历史 JSON 数组解析为可变的 Map 列表
     */
    private List<Map<String, Object>> parseHistoryList(String historyJson) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (StrUtil.isBlank(historyJson)) return list;
        try {
            JSONArray arr = JSONUtil.parseArray(historyJson);
            for (int i = 0; i < arr.size(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                map.put("role", obj.getStr("role"));
                map.put("content", obj.getStr("content"));
                list.add(map);
            }
        } catch (Exception e) {
            log.warn("[parseHistoryList][解析历史消息失败, historyJson={}]", historyJson, e);
        }
        return list;
    }

    private String extractJsonFromContent(String content) {
        if (StrUtil.isBlank(content)) return null;
        int start = content.indexOf("`json");
        if (start < 0) start = content.indexOf("{");
        else start = content.indexOf("{", start);
        int end = content.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return content.substring(start, end + 1);
        }
        return null;
    }
}
