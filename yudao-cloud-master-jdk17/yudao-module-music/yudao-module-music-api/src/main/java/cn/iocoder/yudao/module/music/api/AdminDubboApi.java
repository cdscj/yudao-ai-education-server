package cn.iocoder.yudao.module.music.api;


import cn.iocoder.yudao.module.music.api.dto.UserDTO;

/**
 * 管理员 Dubbo API
 */
public interface AdminDubboApi {

    /**
     * 获取当前登录管理员
     */
    UserDTO getCurrentLoginAdmin();

    /**
     * 校验管理员是否有权限
     */
    boolean hasPermission(String permission);
}