package cn.iocoder.yudao.module.ai.controller.admin.config;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiUserQuotaDO;
import cn.iocoder.yudao.module.ai.service.config.AiUserQuotaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 用户配额")
@RestController
@RequestMapping("/ai/user-quota")
@Validated
public class AiUserQuotaController {

    @Resource
    private AiUserQuotaService userQuotaService;

    @PostMapping("/create")
    @Operation(summary = "创建用户配额")
    @PreAuthorize("@ss.hasPermission('ai:user-quota:create')")
    public CommonResult<Long> createUserQuota(@Valid @RequestBody AiUserQuotaSaveReqVO createReqVO) {
        return success(userQuotaService.createUserQuota(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新用户配额")
    @PreAuthorize("@ss.hasPermission('ai:user-quota:update')")
    public CommonResult<Boolean> updateUserQuota(@Valid @RequestBody AiUserQuotaSaveReqVO updateReqVO) {
        userQuotaService.updateUserQuota(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户配额")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:user-quota:delete')")
    public CommonResult<Boolean> deleteUserQuota(@RequestParam("id") Long id) {
        userQuotaService.deleteUserQuota(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户配额")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:user-quota:query')")
    public CommonResult<AiUserQuotaRespVO> getUserQuota(@RequestParam("id") Long id) {
        AiUserQuotaDO userQuota = userQuotaService.getUserQuota(id);
        return success(BeanUtils.toBean(userQuota, AiUserQuotaRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得用户配额分页")
    @PreAuthorize("@ss.hasPermission('ai:user-quota:query')")
    public CommonResult<PageResult<AiUserQuotaRespVO>> getUserQuotaPage(@Valid AiUserQuotaPageReqVO pageReqVO) {
        PageResult<AiUserQuotaDO> pageResult = userQuotaService.getUserQuotaPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiUserQuotaRespVO.class));
    }

    @PostMapping("/batch-set")
    @Operation(summary = "批量设置用户配额")
    @PreAuthorize("@ss.hasPermission('ai:user-quota:create')")
    public CommonResult<Boolean> batchSetUserQuota(@Valid @RequestBody AiUserQuotaBatchSaveReqVO batchSaveReqVO) {
        userQuotaService.batchSetUserQuota(batchSaveReqVO);
        return success(true);
    }

}
