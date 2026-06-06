package cn.iocoder.yudao.module.auth.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.auth.controller.app.user.vo.AppUserPageReqVO;
import cn.iocoder.yudao.module.auth.dal.dataobject.user.UserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface UserMapper extends BaseMapperX<UserDO> {

    /**
     * 根据手机号查询用户
     */
    default UserDO selectByPhone(String phone) {
        return selectOne(UserDO::getPhone, phone);
    }

    /**
     * 根据用户名查询用户
     */
    default UserDO selectByUsername(String username) {
        return selectOne(UserDO::getUsername, username);
    }

    /**
     * 根据邮箱查询用户
     */
    default UserDO selectByEmail(String email) {
        return selectOne(UserDO::getEmail, email);
    }

    /**
     * 昵称模糊查询
     */
    default List<UserDO> selectListByUsernameLike(String username) {
        return selectList(new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, username));
    }

    /**
     * 分页查询
     */
    default PageResult<UserDO> selectPage(AppUserPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<UserDO>()
                .likeIfPresent(UserDO::getUsername, reqVO.getUsername())
                .likeIfPresent(UserDO::getPhone, reqVO.getPhone())
                .likeIfPresent(UserDO::getEmail, reqVO.getEmail())
                .eqIfPresent(UserDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(UserDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(UserDO::getId));
    }

    /**
     * 根据状态统计数量
     */
    default Long selectCountByStatus(Integer status) {
        return selectCount(UserDO::getStatus, status);
    }

}