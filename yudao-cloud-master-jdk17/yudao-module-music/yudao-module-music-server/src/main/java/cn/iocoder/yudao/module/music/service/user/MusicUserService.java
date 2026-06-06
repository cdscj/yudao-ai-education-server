package cn.iocoder.yudao.module.music.service.user;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.music.controller.admin.user.vo.*;
import cn.iocoder.yudao.module.music.controller.app.user.vo.*;

public interface MusicUserService {

    // ========== 管理后台 ==========
    PageResult<MusicUserPageRespVO> getUserPage(MusicUserPageReqVO reqVO);
    MusicUserRespVO getUser(Long id);
    Long createUser(MusicUserSaveReqVO reqVO);
    void updateUser(MusicUserSaveReqVO reqVO);
    void deleteUser(Long id);

    // ========== App 前台 ==========
    AppMusicUserLoginRespVO login(AppMusicUserLoginReqVO reqVO);
    void register(AppMusicUserRegisterReqVO reqVO);

    // ========== Dubbo 跨模块调用：获取当前登录系统用户 ==========
    MusicUserDTO getCurrentLoginSysUser();

}