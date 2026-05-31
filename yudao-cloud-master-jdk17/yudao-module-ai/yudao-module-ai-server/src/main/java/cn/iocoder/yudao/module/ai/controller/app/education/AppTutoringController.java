package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringChatReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringMessageRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringSessionRespVO;
import cn.iocoder.yudao.module.ai.service.education.AiTutoringService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 智能辅导")
@RestController
@RequestMapping("/ai/education/tutoring")
public class AppTutoringController {

    @Resource
    private AiTutoringService tutoringService;

    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "智能辅导对话")
    public Flux<CommonResult<String>> chat(@RequestBody @Valid TutoringChatReqVO reqVO) {
        return tutoringService.chat(reqVO, getLoginUserId());
    }

    @GetMapping("/sessions")
    @Operation(summary = "获得辅导会话列表")
    public CommonResult<List<TutoringSessionRespVO>> getSessions() {
        return success(tutoringService.getSessions(getLoginUserId()));
    }

    @GetMapping("/messages")
    @Operation(summary = "获得会话消息")
    public CommonResult<List<TutoringMessageRespVO>> getMessages(@RequestParam("sessionId") Long sessionId) {
        return success(tutoringService.getMessages(sessionId));
    }
}
