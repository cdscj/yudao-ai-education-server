package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSystemConfigDO;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 教育配置")
@RestController
@RequestMapping("/ai/education/config")
@Validated
public class AdminAiEducationConfigController {

    @Resource
    private AiSystemConfigService configService;

    @GetMapping("/get/{key}")
    @Operation(summary = "获取配置")
    @PreAuthorize("@ss.hasPermission('ai:education-config:query')")
    public CommonResult<Map<String, Object>> getConfig(@PathVariable("key") String key) {
        AiSystemConfigDO config = configService.getSystemConfigByKey(key);
        if (config == null) {
            Map<String, Object> preset = new HashMap<>();
            preset.put("configKey", key);
            preset.put("configValue", "");
            return success(preset);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("configKey", config.getConfigKey());
        result.put("configValue", config.getConfigValue());
        return success(result);
    }

    @PutMapping("/update")
    @Operation(summary = "更新配置")
    @PreAuthorize("@ss.hasPermission('ai:education-config:update')")
    public CommonResult<Boolean> updateConfig(@RequestBody Map<String, String> body) {
        String key = body.get("configKey");
        String value = body.get("configValue");
        AiSystemConfigDO existing = configService.getSystemConfigByKey(key);
        AiSystemConfigSaveReqVO reqVO = new AiSystemConfigSaveReqVO();
        reqVO.setConfigKey(key);
        reqVO.setConfigValue(value);
        reqVO.setStatus(0);
        if (existing != null) {
            reqVO.setId(existing.getId());
            configService.updateSystemConfig(reqVO);
        } else {
            configService.createSystemConfig(reqVO);
        }
        return success(true);
    }
}
