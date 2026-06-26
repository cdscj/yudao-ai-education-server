package cn.iocoder.yudao.module.ai.controller.admin.agent;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConfigDO;
import cn.iocoder.yudao.module.ai.enums.agent.AiAgentTypeEnum;
import cn.iocoder.yudao.module.ai.service.agent.AiAgentConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;

@Tag(name = "管理后台 - AI Agent 配置")
@RestController
@RequestMapping("/ai/agent-config")
@Validated
public class AiAgentConfigController {

    @Resource
    private AiAgentConfigService agentConfigService;

    @PostMapping("/create")
    @Operation(summary = "创建 Agent 配置")
    @PreAuthorize("@ss.hasPermission('ai:agent-config:create')")
    public CommonResult<Long> createAgentConfig(@Valid @RequestBody AiAgentConfigSaveReqVO createReqVO) {
        return success(agentConfigService.createAgentConfig(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新 Agent 配置")
    @PreAuthorize("@ss.hasPermission('ai:agent-config:update')")
    public CommonResult<Boolean> updateAgentConfig(@Valid @RequestBody AiAgentConfigSaveReqVO updateReqVO) {
        agentConfigService.updateAgentConfig(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除 Agent 配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:agent-config:delete')")
    public CommonResult<Boolean> deleteAgentConfig(@RequestParam("id") Long id) {
        agentConfigService.deleteAgentConfig(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得 Agent 配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:agent-config:query')")
    public CommonResult<AiAgentConfigRespVO> getAgentConfig(@RequestParam("id") Long id) {
        AiAgentConfigDO config = agentConfigService.getAgentConfig(id);
        return success(BeanUtils.toBean(config, AiAgentConfigRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得 Agent 配置分页")
    @PreAuthorize("@ss.hasPermission('ai:agent-config:query')")
    public CommonResult<PageResult<AiAgentConfigRespVO>> getAgentConfigPage(
            @Valid AiAgentConfigPageReqVO pageReqVO) {
        PageResult<AiAgentConfigDO> pageResult = agentConfigService.getAgentConfigPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiAgentConfigRespVO.class));
    }

    @GetMapping("/simple-list")
    @Operation(summary = "获得启用的 Agent 配置列表")
    public CommonResult<List<AiAgentConfigRespVO>> getAgentConfigSimpleList(
            @RequestParam(value = "type", required = false) Integer type) {
        List<AiAgentConfigDO> list = agentConfigService.getAgentConfigListByStatusAndType(
                CommonStatusEnum.ENABLE.getStatus(), type);
        return success(convertList(list, config -> new AiAgentConfigRespVO()
                .setId(config.getId()).setName(config.getName())
                .setType(config.getType()).setDescription(config.getDescription())
                .setModelId(config.getModelId())));
    }

    @GetMapping("/type-list")
    @Operation(summary = "获得 Agent 类型列表")
    public CommonResult<List<Map<String, Object>>> getAgentTypeList() {
        List<Map<String, Object>> types = Arrays.stream(AiAgentTypeEnum.values())
                .map(t -> Map.<String, Object>of("type", t.getType(), "code", t.getCode(), "name", t.getName()))
                .toList();
        return success(types);
    }
}
