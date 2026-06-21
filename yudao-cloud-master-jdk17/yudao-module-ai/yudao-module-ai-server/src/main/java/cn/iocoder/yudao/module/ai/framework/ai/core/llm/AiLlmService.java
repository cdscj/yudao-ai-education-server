package cn.iocoder.yudao.module.ai.framework.ai.core.llm;

import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.gateway.AiModelGateway;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * AI LLM 调用服务 — 重构后委托给 {@link AiModelGateway}
 *
 * <p>本类保持原有 API 签名不变以兼容现有调用方，
 * 内部全部委托给 AiModelGateway 处理重试/熔断/fallback。</p>
 *
 * @author fansili
 */
@Service
@Slf4j
public class AiLlmService {

    @Resource
    private AiModelService modalService;
    @Resource
    private AiModelGateway modelGateway;

    /**
     * 流式对话（使用默认 Chat 模型）
     */
    public Flux<String> chatStream(String systemPrompt, String userInput, String context) {
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        return modelGateway.chatStream(model.getId(), systemPrompt, userInput, context);
    }

    /**
     * 同步对话（使用默认 Chat 模型）
     */
    public String chatSync(String systemPrompt, String userInput, String context) {
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        return modelGateway.chatSync(model.getId(), systemPrompt, userInput, context);
    }

    /**
     * 同步对话（指定模型ID）
     */
    public String chatSync(Long modelId, String systemPrompt, String userInput, String context) {
        return modelGateway.chatSync(modelId, systemPrompt, userInput, context);
    }

    /**
     * 流式对话（指定模型ID）
     */
    public Flux<String> chatStream(Long modelId, String systemPrompt, String userInput, String context) {
        return modelGateway.chatStream(modelId, systemPrompt, userInput, context);
    }
}
