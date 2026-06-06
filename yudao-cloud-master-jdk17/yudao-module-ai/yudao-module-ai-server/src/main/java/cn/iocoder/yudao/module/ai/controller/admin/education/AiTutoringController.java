package cn.iocoder.yudao.module.ai.controller.admin.education;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - AI 智能辅导")
@RestController
@RequestMapping("/ai/education/tutoring")
@Validated
public class AiTutoringController {

    @Resource
    private AiTutoringService tutoringService;

    @PostMapping(value = "/chat-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "智能辅导对话（流式）")
    @PreAuthorize("@ss.hasPermission('ai:tutoring:query')")
    public Flux<CommonResult<String>> chat(@RequestBody @Valid TutoringChatReqVO reqVO) {
        return tutoringService.chat(reqVO, getLoginUserId());
    }

    @GetMapping("/sessions")
    @Operation(summary = "获取辅导会话列表")
    @PreAuthorize("@ss.hasPermission('ai:tutoring:query')")
    public CommonResult<List<TutoringSessionRespVO>> getSessions() {
        return success(tutoringService.getSessions(getLoginUserId()));
    }

    @GetMapping("/messages")
    @Operation(summary = "获取会话消息")
    @Parameter(name = "sessionId", description = "会话编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:tutoring:query')")
    public CommonResult<List<TutoringMessageRespVO>> getMessages(@RequestParam("sessionId") Long sessionId) {
        return success(tutoringService.getMessages(sessionId));
    }

    @DeleteMapping("/delete-session")
    @Operation(summary = "删除辅导会话")
    @Parameter(name = "id", description = "会话编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:tutoring:delete')")
    public CommonResult<Boolean> deleteSession(@RequestParam("id") Long id) {
        tutoringService.deleteSession(id);
        return success(true);
    }
}
