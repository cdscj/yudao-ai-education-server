package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.service.education.AiStudyDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 学习数据看板")
@RestController
@RequestMapping("/ai/education/study-data")
@Validated
public class AppAiStudyDataController {

    @Resource private AiStudyDataService studyDataService;

    @GetMapping("/dashboard")
    @Operation(summary = "学习数据看板")
    public CommonResult<Map<String, Object>> dashboard() {
        return success(studyDataService.getDashboardData(getLoginUserId()));
    }
}
