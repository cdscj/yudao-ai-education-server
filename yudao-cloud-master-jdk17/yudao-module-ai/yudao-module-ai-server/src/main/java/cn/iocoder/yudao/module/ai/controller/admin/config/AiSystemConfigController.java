package cn.iocoder.yudao.module.ai.controller.admin.config;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSystemConfigDO;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 系统配置")
@RestController
@RequestMapping("/ai/system-config")
@Validated
public class AiSystemConfigController {

    @Resource
    private AiSystemConfigService systemConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建系统配置")
    @PreAuthorize("@ss.hasPermission('ai:system-config:create')")
    public CommonResult<Long> createSystemConfig(@Valid @RequestBody AiSystemConfigSaveReqVO createReqVO) {
        return success(systemConfigService.createSystemConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新系统配置")
    @PreAuthorize("@ss.hasPermission('ai:system-config:update')")
    public CommonResult<Boolean> updateSystemConfig(@Valid @RequestBody AiSystemConfigSaveReqVO updateReqVO) {
        systemConfigService.updateSystemConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除系统配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:system-config:delete')")
    public CommonResult<Boolean> deleteSystemConfig(@RequestParam("id") Long id) {
        systemConfigService.deleteSystemConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得系统配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:system-config:query')")
    public CommonResult<AiSystemConfigRespVO> getSystemConfig(@RequestParam("id") Long id) {
        AiSystemConfigDO systemConfig = systemConfigService.getSystemConfig(id);
        return success(BeanUtils.toBean(systemConfig, AiSystemConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得系统配置分页")
    @PreAuthorize("@ss.hasPermission('ai:system-config:query')")
    public CommonResult<PageResult<AiSystemConfigRespVO>> getSystemConfigPage(@Valid AiSystemConfigPageReqVO pageReqVO) {
        PageResult<AiSystemConfigDO> pageResult = systemConfigService.getSystemConfigPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiSystemConfigRespVO.class));
    }

    @PostMapping("/refresh-cache")
    @Operation(summary = "刷新系统配置缓存")
    @PreAuthorize("@ss.hasPermission('ai:system-config:update')")
    public CommonResult<Boolean> refreshConfigCache() {
        systemConfigService.refreshConfigCache();
        return success(true);
    }

    @GetMapping("/value")
    @Operation(summary = "根据配置键获取配置值")
    @Parameter(name = "key", description = "配置键", required = true, example = "ai.default.model")
    @PreAuthorize("@ss.hasPermission('ai:system-config:query')")
    public CommonResult<String> getConfigValueByKey(@RequestParam("key") String key) {
        String value = systemConfigService.getConfigValue(key, null);
        return success(value);
    }

}
