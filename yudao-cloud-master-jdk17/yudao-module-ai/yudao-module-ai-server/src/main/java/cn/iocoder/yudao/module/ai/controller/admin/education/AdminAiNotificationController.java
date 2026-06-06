package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.service.education.AiNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 消息通知")
@RestController
@RequestMapping("/ai/notification")
@Validated
public class AdminAiNotificationController {

    @Resource private AiNotificationService service;

    @PostMapping("/send") @Operation(summary = "发送通知")
    @PreAuthorize("@ss.hasPermission('ai:notification:create')")
    public CommonResult<Boolean> send(@RequestBody Map<String, Object> body) {
        Long userId = Long.valueOf(body.get("userId").toString());
        String type = body.get("type").toString();
        String title = body.get("title").toString();
        String content = body.getOrDefault("content", "").toString();
        service.createNotification(userId, type, title, content, null, null);
        return success(true);
    }

    @GetMapping("/page") @Operation(summary = "通知分页")
    @PreAuthorize("@ss.hasPermission('ai:notification:query')")
    public CommonResult<?> page(@RequestParam(value="userId",required=false) Long userId,
            @RequestParam(value="pageNo",defaultValue="1") Integer pageNo,
            @RequestParam(value="pageSize",defaultValue="10") Integer pageSize) {
        return success(service.getPage(userId, null, pageNo, pageSize));
    }
}
