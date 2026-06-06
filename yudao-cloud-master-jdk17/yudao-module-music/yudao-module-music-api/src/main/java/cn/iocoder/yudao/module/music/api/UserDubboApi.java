package cn.iocoder.yudao.module.music.api;


import cn.iocoder.yudao.module.music.api.dto.UserDTO;

/**
 * 用户模块 Dubbo API
 */
public interface UserDubboApi {

    /**
     * 根据用户 ID 查询用户信息
     */
    UserDTO getUserById(Long userId);

    /**
     * 根据用户名查询用户
     */
    UserDTO getUserByUsername(String username);
}