package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFriendDO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiFriendMapper extends BaseMapperX<AiFriendDO> {

    default AiFriendDO selectByUserIdAndFriendUserId(Long userId, Long friendUserId) {
        return selectOne(new LambdaQueryWrapperX<AiFriendDO>()
                .eq(AiFriendDO::getUserId, userId)
                .eq(AiFriendDO::getFriendUserId, friendUserId));
    }

    default List<AiFriendDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiFriendDO>()
                .eq(AiFriendDO::getUserId, userId)
                .orderByDesc(AiFriendDO::getCreateTime));
    }

    default List<AiFriendDO> selectListByUserIdAndStatus(Long userId, Integer status) {
        return selectList(new LambdaQueryWrapperX<AiFriendDO>()
                .eq(AiFriendDO::getUserId, userId)
                .eq(AiFriendDO::getStatus, status)
                .orderByDesc(AiFriendDO::getCreateTime));
    }

    default List<AiFriendDO> selectListByFriendUserIdAndStatus(Long friendUserId, Integer status) {
        return selectList(new LambdaQueryWrapperX<AiFriendDO>()
                .eq(AiFriendDO::getFriendUserId, friendUserId)
                .eq(AiFriendDO::getStatus, status)
                .orderByDesc(AiFriendDO::getCreateTime));
    }

    default void deleteByUserIdAndFriendUserId(Long userId, Long friendUserId) {
        delete(new LambdaQueryWrapperX<AiFriendDO>()
                .eq(AiFriendDO::getUserId, userId)
                .eq(AiFriendDO::getFriendUserId, friendUserId));
    }

    default PageResult<AiFriendDO> selectPage(Long userId, Long friendUserId, Integer status, PageParam pageParam) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiFriendDO>()
                .eqIfPresent(AiFriendDO::getUserId, userId)
                .eqIfPresent(AiFriendDO::getFriendUserId, friendUserId)
                .eqIfPresent(AiFriendDO::getStatus, status)
                .orderByDesc(AiFriendDO::getId));
    }

    @Delete("DELETE FROM ai_friend WHERE id = #{id}")
    int deleteByIdForce(Long id);
}
