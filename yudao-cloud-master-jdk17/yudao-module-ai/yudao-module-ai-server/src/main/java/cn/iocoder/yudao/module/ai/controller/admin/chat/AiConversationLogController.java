package cn.iocoder.yudao.module.ai.controller.admin.chat;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.log.AiConversationLogPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.log.AiConversationLogRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiConversationLogDO;
import cn.iocoder.yudao.module.ai.service.chat.AiConversationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 对话日志")
@RestController
@RequestMapping("/ai/conversation-log")
@Validated
public class AiConversationLogController {

    @Resource
    private AiConversationLogService conversationLogService;

    @GetMapping("/page")
    @Operation(summary = "获得对话日志分页")
    @PreAuthorize("@ss.hasPermission('ai:conversation-log:query')")
    public CommonResult<PageResult<AiConversationLogRespVO>> getConversationLogPage(@Valid AiConversationLogPageReqVO pageReqVO) {
        PageResult<AiConversationLogDO> pageResult = conversationLogService.getConversationLogPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiConversationLogRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得对话日志详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:conversation-log:query')")
    public CommonResult<AiConversationLogRespVO> getConversationLog(@RequestParam("id") Long id) {
        AiConversationLogDO conversationLog = conversationLogService.getConversationLog(id);
        return success(BeanUtils.toBean(conversationLog, AiConversationLogRespVO.class));
    }

    @GetMapping("/count")
    @Operation(summary = "获得对话日志数量")
    @Parameter(name = "startDate", description = "开始日期", example = "2024-01-01")
    @Parameter(name = "endDate", description = "结束日期", example = "2024-01-31")
    @PreAuthorize("@ss.hasPermission('ai:conversation-log:query')")
    public CommonResult<Long> getConversationLogCount(@RequestParam(value = "startDate", required = false) String startDate,
                                                      @RequestParam(value = "endDate", required = false) String endDate) {
        return success(conversationLogService.getConversationLogCount(startDate, endDate));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除对话日志")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:conversation-log:delete')")
    public CommonResult<Boolean> deleteConversationLog(@RequestParam("id") Long id) {
        conversationLogService.deleteConversationLog(id);
        return success(true);
    }

}
