package cn.iocoder.yudao.module.music.service.user.Impl;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.music.api.dto.MusicUserDTO;
import cn.iocoder.yudao.module.music.controller.admin.user.vo.*;
import cn.iocoder.yudao.module.music.controller.app.user.vo.*;
import cn.iocoder.yudao.module.music.dal.mysql.user.MusicUserMapper;
import cn.iocoder.yudao.module.music.service.user.MusicUserService;
import cn.iocoder.yudao.module.system.api.user.AdminUserApi;
import cn.iocoder.yudao.module.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Service;


@Service
public class MusicUserServiceImpl implements MusicUserService {

    // Dubbo 调用 System 模块用户接口（你框架有 Dubbo，直接用）
    @DubboReference(version = "1.0.0", check = false)
    private AdminUserApi adminUserApi;

    @Resource
    private MusicUserMapper musicUserMapper;

    // ========== 管理后台 ==========
    @Override
    public PageResult<MusicUserPageRespVO> getUserPage(MusicUserPageReqVO reqVO) {
        return PageResult.empty();
    }

    @Override
    public MusicUserRespVO getUser(Long id) {
        return null;
    }

    @Override
    public Long createUser(MusicUserSaveReqVO reqVO) {
        return null;
    }

    @Override
    public void updateUser(MusicUserSaveReqVO reqVO) {

    }

    @Override
    public void deleteUser(Long id) {

    }

    // ========== App 前台 ==========
    @Override
    public AppMusicUserLoginRespVO login(AppMusicUserLoginReqVO reqVO) {
        return null;
    }

    @Override
    public void register(AppMusicUserRegisterReqVO reqVO) {

    }

    // ========== Dubbo 跨模块调用 ==========
    @Override
    public MusicUserDTO getCurrentLoginSysUser() {
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        if (userId == null) {
            return null;
        }
        // Dubbo 远程调用 system
        AdminUserRespDTO sysUser = adminUserApi.getUser(userId);
        return convert(sysUser);
    }

    // DTO 转换
    private MusicUserDTO convert(AdminUserRespDTO sysUser) {
        MusicUserDTO dto = new MusicUserDTO();
        dto.setId(sysUser.getId());
        dto.setUsername(sysUser.getUsername());
        dto.setNickname(sysUser.getNickname());
        dto.setEmail(sysUser.getEmail());
        dto.setMobile(sysUser.getMobile());
        dto.setStatus(sysUser.getStatus());
        return dto;
    }

}