package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.service.education.AiNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 消息通知")
@RestController
@RequestMapping("/ai/notification")
@Validated
public class AppAiNotificationController {

    @Resource private AiNotificationService service;

    @GetMapping("/page") @Operation(summary = "通知分页")
    public CommonResult<PageResult<?>> page(@RequestParam(value="pageNo",defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize",defaultValue="10") Integer pageSize) {
        return success(service.getPage(getLoginUserId(), null, pageNo, pageSize));
    }

    @GetMapping("/unread-count") @Operation(summary = "未读数量")
    public CommonResult<Long> unreadCount() { return success(service.getUnreadCount(getLoginUserId())); }

    @PutMapping("/mark-read") @Operation(summary = "标记已读")
    public CommonResult<Boolean> markRead(@RequestParam("id") Long id) {
        service.markRead(id, getLoginUserId()); return success(true);
    }

    @PutMapping("/mark-all-read") @Operation(summary = "全部已读")
    public CommonResult<Boolean> markAllRead() { service.markAllRead(getLoginUserId()); return success(true); }
}
