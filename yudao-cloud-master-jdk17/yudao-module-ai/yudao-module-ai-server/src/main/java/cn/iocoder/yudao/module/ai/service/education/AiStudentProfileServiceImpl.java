package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.StudentProfileChatReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudentProfileDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiChatRoleDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiStudentProfileMapper;
import cn.iocoder.yudao.module.ai.enums.AiChatRoleEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.model.AiChatRoleService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

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
    private AiModelService modalService;
    @Resource
    private AiChatRoleService chatRoleService;

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
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        ChatModel chatModel = modalService.getChatModel(model.getId());

        AiStudentProfileDO existingProfile = studentProfileMapper.selectByUserId(userId);
        String contextJson = existingProfile != null ? existingProfile.getProfileJson() : "{}";

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(PROFILE_SYSTEM_PROMPT));
        messages.add(new UserMessage("当前已有画像数据：" + contextJson + "。用户新消息：" + chatReqVO.getMessage()));

        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                model.getTemperature(), model.getMaxTokens());
        Prompt prompt = new Prompt(messages, options);
        Flux<ChatResponse> streamResponse = chatModel.stream(prompt);

        StringBuffer contentBuffer = new StringBuffer();
        return streamResponse.map(chunk -> {
            String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : "";
            contentBuffer.append(newContent);
            return success(newContent);
        }).doOnComplete(() -> {
            TenantUtils.executeIgnore(() -> {
                String fullContent = contentBuffer.toString();
                String jsonStr = extractJsonFromContent(fullContent);
                if (jsonStr != null && existingProfile != null) {
                    existingProfile.setProfileJson(jsonStr);
                    studentProfileMapper.updateById(existingProfile);
                } else if (jsonStr != null) {
                    AiStudentProfileDO newProfile = new AiStudentProfileDO();
                    newProfile.setUserId(userId);
                    newProfile.setProfileJson(jsonStr);
                    studentProfileMapper.insert(newProfile);
                }
            });
        }).doOnError(throwable -> {
            log.error("[chatBuildProfile][userId({}) 发生异常]", userId, throwable);
        }).onErrorResume(error -> Flux.just(error(EDUCATION_STREAM_ERROR)));
    }

    @Override
    public AiStudentProfileDO updateProfile(Long id, AiStudentProfileDO profile) {
        studentProfileMapper.updateById(profile);
        return studentProfileMapper.selectById(id);
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
