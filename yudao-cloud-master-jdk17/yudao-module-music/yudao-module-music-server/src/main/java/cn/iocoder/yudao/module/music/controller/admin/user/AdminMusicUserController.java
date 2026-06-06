package cn.iocoder.yudao.module.music.controller.admin.user;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.music.controller.admin.user.vo.*;
import cn.iocoder.yudao.module.music.service.user.MusicUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "管理后台 - 音乐用户")
@RestController
@RequestMapping("/admin-api/music/user")
public class AdminMusicUserController {

    @Resource
    private MusicUserService musicUserService;

    @GetMapping("/page")
    @Operation(summary = "用户分页")
    @PreAuthorize("@ss.hasPermission('music:user:query')")
    public CommonResult<PageResult<MusicUserPageRespVO>> page(@Valid MusicUserPageReqVO reqVO) {
        return CommonResult.success(musicUserService.getUserPage(reqVO));
    }

    @GetMapping("/get")
    @Operation(summary = "用户详情")
    @PreAuthorize("@ss.hasPermission('music:user:query')")
    public CommonResult<MusicUserRespVO> get(@RequestParam Long id) {
        return CommonResult.success(musicUserService.getUser(id));
    }

    @PostMapping("/create")
    @Operation(summary = "新增用户")
    @PreAuthorize("@ss.hasPermission('music:user:create')")
    public CommonResult<Long> create(@Valid @RequestBody MusicUserSaveReqVO reqVO) {
        return CommonResult.success(musicUserService.createUser(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用户")
    @PreAuthorize("@ss.hasPermission('music:user:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody MusicUserSaveReqVO reqVO) {
        musicUserService.updateUser(reqVO);
        return CommonResult.success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除用户")
    @PreAuthorize("@ss.hasPermission('music:user:delete')")
    public CommonResult<Boolean> delete(@RequestParam Long id) {
        musicUserService.deleteUser(id);
        return CommonResult.success(true);
    }

}