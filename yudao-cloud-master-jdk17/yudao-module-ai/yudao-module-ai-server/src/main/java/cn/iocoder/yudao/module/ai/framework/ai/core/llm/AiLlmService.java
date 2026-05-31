package cn.iocoder.yudao.module.ai.framework.ai.core.llm;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Service
@Slf4j
public class AiLlmService {

    @Resource
    private AiModelService modalService;

    public Flux<String> chatStream(String systemPrompt, String userInput, String context) {
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        ChatModel chatModel = modalService.getChatModel(model.getId());

        Prompt prompt = buildPrompt(systemPrompt, userInput, context, model);
        Flux<ChatResponse> stream = chatModel.stream(prompt);

        return stream.map(chunk -> {
            String content = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : "";
            return StrUtil.nullToDefault(content, "");
        }).onErrorResume(e -> {
            log.error("[chatStream] LLM调用异常", e);
            return Flux.just("【AI服务暂时不可用，请稍后重试】");
        });
    }

    public String chatSync(String systemPrompt, String userInput, String context) {
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        ChatModel chatModel = modalService.getChatModel(model.getId());

        Prompt prompt = buildPrompt(systemPrompt, userInput, context, model);
        ChatResponse response = chatModel.call(prompt);
        String content = AiUtils.getChatResponseContent(response);
        return StrUtil.nullToDefault(content, "");
    }

    private Prompt buildPrompt(String systemPrompt, String userInput, String context, AiModelDO model) {
        var messages = new java.util.ArrayList<org.springframework.ai.chat.messages.Message>();
        messages.add(new SystemMessage(systemPrompt));
        if (StrUtil.isNotBlank(context)) {
            messages.add(new SystemMessage("历史上下文摘要：" + context));
        }
        messages.add(new UserMessage(userInput));

        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                model.getTemperature(), model.getMaxTokens());
        return new Prompt(messages, options);
    }
}
