package cn.iocoder.yudao.module.ai.controller.admin.agent;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConversationPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConversationRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentMessageRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConversationDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentMessageDO;
import cn.iocoder.yudao.module.ai.service.agent.AiAgentConversationService;
import cn.iocoder.yudao.module.ai.service.agent.AiAgentMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - AI Agent 会话")
@RestController
@RequestMapping("/ai/agent-conversation")
@Validated
public class AiAgentConversationController {

    @Resource
    private AiAgentConversationService conversationService;
    @Resource
    private AiAgentMessageService messageService;

    @GetMapping("/page")
    @Operation(summary = "获得 Agent 会话分页")
    @PreAuthorize("@ss.hasPermission('ai:agent-conversation:query')")
    public CommonResult<PageResult<AiAgentConversationRespVO>> getConversationPage(
            @Valid AiAgentConversationPageReqVO pageReqVO) {
        PageResult<AiAgentConversationDO> pageResult = conversationService.getConversationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiAgentConversationRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得 Agent 会话")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:agent-conversation:query')")
    public CommonResult<AiAgentConversationRespVO> getConversation(@RequestParam("id") Long id) {
        AiAgentConversationDO conversation = conversationService.getConversation(id);
        return success(BeanUtils.toBean(conversation, AiAgentConversationRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除（结束）Agent 会话")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:agent-conversation:delete')")
    public CommonResult<Boolean> deleteConversation(@RequestParam("id") Long id) {
        conversationService.deleteConversation(id);
        return success(true);
    }

    @GetMapping("/messages")
    @Operation(summary = "获得会话的消息列表")
    @Parameter(name = "conversationId", description = "会话编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:agent-conversation:query')")
    public CommonResult<List<AiAgentMessageRespVO>> getMessages(
            @RequestParam("conversationId") Long conversationId) {
        List<AiAgentMessageDO> messages = messageService.getMessageList(conversationId);
        return success(BeanUtils.toBean(messages, AiAgentMessageRespVO.class));
    }

    @GetMapping("/list-by-user")
    @Operation(summary = "获取当前用户的 Agent 会话列表")
    @Parameter(name = "userId", description = "用户编号", required = true, example = "1")
    public CommonResult<List<AiAgentConversationRespVO>> getConversationListByUser(
            @RequestParam("userId") Long userId) {
        List<AiAgentConversationDO> list = conversationService.getConversationListByUserId(userId);
        return success(convertList(list, conv -> new AiAgentConversationRespVO()
                .setId(conv.getId()).setTitle(conv.getTitle())
                .setAgentId(conv.getAgentId()).setModelId(conv.getModelId())
                .setMessageCount(conv.getMessageCount())
                .setActive(conv.getActive())
                .setCreateTime(conv.getCreateTime())));
    }
}
