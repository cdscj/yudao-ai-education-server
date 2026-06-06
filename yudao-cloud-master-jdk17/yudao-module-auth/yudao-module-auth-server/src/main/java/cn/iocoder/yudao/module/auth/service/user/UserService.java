package cn.iocoder.yudao.module.auth.service.user;

import cn.iocoder.yudao.framework.common.enums.TerminalEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.auth.controller.app.user.vo.*;
import cn.iocoder.yudao.module.auth.dal.dataobject.user.UserDO;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.List;

/**
 * 用户 Service 接口
 *
 * @author 芋道源码
 */
public interface UserService {

    /**
     * 通过邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象
     */
    UserDO getUserByEmail(String email);

    /**
     * 基于用户名，模糊匹配用户列表
     *
     * @param username 用户名，模糊匹配
     * @return 用户信息的列表
     */
    List<UserDO> getUserListByUsername(String username);

    /**
     * 基于邮箱创建用户。
     * 如果用户已经存在，则直接进行返回
     *
     * @param email     邮箱
     * @param registerIp 注册 IP
     * @param terminal   终端 {@link TerminalEnum}
     * @return 用户对象
     */
    UserDO createUserIfAbsent(String email, String registerIp, Integer terminal);

    /**
     * 创建用户
     * 目的：三方登录时，如果未绑定用户时，自动创建对应用户
     *
     * @param username   用户名
     * @param avatar      头像
     * @param registerIp 注册 IP
     * @param terminal   终端 {@link TerminalEnum}
     * @return 用户对象
     */
    UserDO createUser(String username, String avatar, String registerIp, Integer terminal);

    /**
     * 更新用户的最后登陆信息
     *
     * @param id      用户编号
     * @param loginIp 登陆 IP
     */
    void updateUserLogin(Long id, String loginIp);

    /**
     * 通过用户 ID 查询用户
     *
     * @param id 用户ID
     * @return 用户对象信息
     */
    UserDO getUser(Long id);

    /**
     * 通过用户 ID 查询用户们
     *
     * @param ids 用户 ID
     * @return 用户对象信息数组
     */
    List<UserDO> getUserList(Collection<Long> ids);

    /**
     * 【用户】修改基本信息
     *
     * @param userId 用户编号
     * @param reqVO  基本信息
     */
    void updateUser(Long userId, AppUserUpdateReqVO reqVO);

    /**
     * 【用户】修改邮箱，基于邮箱验证码
     *
     * @param userId 用户编号
     * @param reqVO  请求信息
     */
    void updateUserEmail(Long userId, AppUserUpdateEmailReqVO reqVO);

    /**
     * 【用户】修改密码
     *
     * @param userId 用户编号
     * @param reqVO  请求信息
     */
    void updateUserPassword(Long userId, AppUserUpdatePasswordReqVO reqVO);

    /**
     * 【用户】忘记密码
     *
     * @param reqVO 请求信息
     */
    void resetUserPassword(AppUserResetPasswordReqVO reqVO);

    /**
     * 判断密码是否匹配
     *
     * @param rawPassword     未加密的密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    boolean isPasswordMatch(String rawPassword, String encodedPassword);

    /**
     * 【管理员】更新用户
     *
     * @param updateReqVO 更新信息
     */
    void updateUser(@Valid AppUserUpdateReqVO updateReqVO);

    /**
     * 【管理员】获得用户分页
     *
     * @param pageReqVO 分页查询
     * @return 用户分页
     */
    PageResult<UserDO> getUserPage(AppUserPageReqVO pageReqVO);

}