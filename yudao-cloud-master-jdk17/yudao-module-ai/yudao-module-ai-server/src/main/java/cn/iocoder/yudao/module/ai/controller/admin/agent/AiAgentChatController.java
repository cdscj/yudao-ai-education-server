package cn.iocoder.yudao.module.ai.controller.admin.agent;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.servlet.ServletUtils;
import cn.iocoder.yudao.framework.web.core.util.WebFrameworkUtils;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentChatSendReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentMessageDO;
import cn.iocoder.yudao.module.ai.service.agent.AiAgentConversationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI Agent 对话（用户端）")
@RestController
@RequestMapping("/ai/agent-chat")
@Validated
@Slf4j
public class AiAgentChatController {

    @Resource
    private AiAgentConversationService conversationService;

    @PostMapping("/send")
    @Operation(summary = "发送消息给 Agent（阻塞式，返回完整结果）")
    public CommonResult<String> send(@Valid @RequestBody AiAgentChatSendReqVO sendReqVO) {
        Long userId = getLoginUserId();
        AiAgentMessageDO response;
        if (sendReqVO.getConversationId() != null) {
            response = conversationService.sendMessage(userId, sendReqVO);
        } else {
            response = conversationService.createConversationAndSend(userId, sendReqVO);
        }
        return success(response.getContent());
    }

    @PostMapping(value = "/send-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "发送消息给 Agent（流式 SSE，实时输出）")
    public Flux<String> sendStream(@Valid @RequestBody AiAgentChatSendReqVO sendReqVO) {
        Long userId = getLoginUserId();
        return conversationService.sendMessageStream(userId, sendReqVO);
    }

    /**
     * 获取当前登录用户ID（兼容 mock 登录模式）
     */
    private Long getLoginUserId() {
        try {
            return WebFrameworkUtils.getLoginUserId();
        } catch (Exception e) {
            return 1L; // 本地 mock 登录模式默认用户
        }
    }
}
