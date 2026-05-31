package cn.iocoder.yudao.module.ai.service.education;

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
public class AiTutoringServiceImpl implements AiTutoringService {

    @Resource
    private AiTutoringSessionMapper tutoringSessionMapper;
    @Resource
    private AiTutoringMessageMapper tutoringMessageMapper;
    @Resource
    private AiModelService modalService;

    private static final String TUTOR_SYSTEM_PROMPT = """
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
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        ChatModel chatModel = modalService.getChatModel(model.getId());

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
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(TUTOR_SYSTEM_PROMPT));
        for (AiTutoringMessageDO msg : history) {
            messages.add(new UserMessage(msg.getContent()));
        }

        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                model.getTemperature(), model.getMaxTokens());
        Prompt prompt = new Prompt(messages, options);
        Flux<ChatResponse> streamResponse = chatModel.stream(prompt);

        Long finalSessionId = sessionId;
        AiTutoringSessionDO finalSession = session;
        StringBuffer contentBuffer = new StringBuffer();
        return streamResponse.map(chunk -> {
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
        }).doOnError(throwable -> {
            log.error("[tutoringChat][sessionId({}) 异常]", finalSessionId, throwable);
        }).onErrorResume(error -> Flux.just(error(EDUCATION_STREAM_ERROR)));
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
