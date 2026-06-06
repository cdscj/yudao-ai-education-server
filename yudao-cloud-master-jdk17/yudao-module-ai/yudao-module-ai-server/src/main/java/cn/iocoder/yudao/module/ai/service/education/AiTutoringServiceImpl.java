package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringChatReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringMessageRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringSessionRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiTutoringMessageDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiTutoringSessionDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiTutoringMessageMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiTutoringSessionMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
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
public class AiTutoringServiceImpl implements AiTutoringService {

    @Resource
    private AiTutoringSessionMapper tutoringSessionMapper;
    @Resource
    private AiTutoringMessageMapper tutoringMessageMapper;
    @Resource
    private AiModelService modelService;
    @Resource
    private AiSystemConfigService configService;

    private static final String DEFAULT_TUTOR_PROMPT = """
            你是一位专业、耐心的高校课程辅导教师。你的职责包括：
            1. 解答学生关于计算机、人工智能、电子信息类课程的问题
            2. 提供多形式解答：文字详解、图解思路、代码示例等
            3. 引导学生自己思考，而不是直接给答案
            4. 根据学生问题难度，由浅入深讲解
            5. 使用Markdown格式输出，代码用代码块标注

            回答风格：友好、鼓励、专业。适当使用emoji增加亲和力。
            """;

    @Override
    public Flux<CommonResult<String>> chat(TutoringChatReqVO reqVO, Long userId) {
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            return Flux.just(error(MODEL_DEFAULT_NOT_EXISTS));
        }

        Long sessionId = reqVO.getSessionId();
        AiTutoringSessionDO session;

        if (sessionId == null || sessionId == 0) {
            session = new AiTutoringSessionDO();
            session.setUserId(userId).setTitle(StrUtil.sub(reqVO.getQuestion(), 0, 50)).setQuestion(reqVO.getQuestion()).setStatus("ACTIVE");
            tutoringSessionMapper.insert(session);
            sessionId = session.getId();
        } else {
            session = tutoringSessionMapper.selectById(sessionId);
            if (session == null) {
                return Flux.just(error(TUTORING_SESSION_NOT_EXISTS));
            }
        }

        AiTutoringMessageDO userMsg = new AiTutoringMessageDO();
        userMsg.setSessionId(sessionId).setUserId(userId).setRole("user").setContentType("text").setContent(reqVO.getQuestion());
        tutoringMessageMapper.insert(userMsg);

        List<AiTutoringMessageDO> history = tutoringMessageMapper.selectListBySessionId(sessionId);
        // 限制最近 50 条历史消息，防止超出 token 限制（new ArrayList 避免 subList 视图持有原始大 List 引用）
        if (history.size() > 50) { history = new ArrayList<>(history.subList(history.size() - 50, history.size())); }
        List<Message> messages = new ArrayList<>();
        String tutorPrompt = configService.getConfigValue("edu.tutoring.prompt", DEFAULT_TUTOR_PROMPT);
        messages.add(new SystemMessage(tutorPrompt));
        for (AiTutoringMessageDO msg : history) {
            messages.add(new UserMessage(msg.getContent()));
        }

        Long finalSessionId = sessionId;
        AiTutoringSessionDO finalSession = session;
        return AiUtils.buildStreamWithFallback(models, model -> {
            ChatModel chatModel = modelService.getChatModel(model.getId());
            AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
            ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                    model.getTemperature(), model.getMaxTokens());
            Prompt prompt = new Prompt(messages, options);

            StringBuffer contentBuffer = new StringBuffer();
            return chatModel.stream(prompt).map(chunk -> {
                String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : "";
                contentBuffer.append(newContent);
                return success(newContent);
            }).doOnComplete(() -> {
                TenantUtils.executeIgnore(() -> {
                    AiTutoringMessageDO assistantMsg = new AiTutoringMessageDO();
                    assistantMsg.setSessionId(finalSessionId).setUserId(userId).setRole("assistant")
                            .setContentType("text").setContent(contentBuffer.toString());
                    tutoringMessageMapper.insert(assistantMsg);
                    if (finalSession.getTitle() == null || finalSession.getTitle().length() < 3) {
                        tutoringSessionMapper.updateById(new AiTutoringSessionDO()
                                .setId(finalSessionId).setTitle(StrUtil.sub(reqVO.getQuestion(), 0, 50)));
                    }
                });
            });
        }, "tutoringChat", EDUCATION_STREAM_ERROR);
    }

    @Override
    public List<TutoringSessionRespVO> getSessions(Long userId) {
        List<AiTutoringSessionDO> list = tutoringSessionMapper.selectListByUserId(userId);
        return BeanUtils.toBean(list, TutoringSessionRespVO.class);
    }

    @Override
    public List<TutoringMessageRespVO> getMessages(Long sessionId) {
        List<AiTutoringMessageDO> list = tutoringMessageMapper.selectListBySessionId(sessionId);
        return BeanUtils.toBean(list, TutoringMessageRespVO.class);
    }

    @Override
    public void deleteSession(Long id) {
        if (tutoringSessionMapper.selectById(id) == null) {
            throw exception(TUTORING_SESSION_NOT_EXISTS);
        }
        tutoringSessionMapper.deleteById(id);
    }
}
