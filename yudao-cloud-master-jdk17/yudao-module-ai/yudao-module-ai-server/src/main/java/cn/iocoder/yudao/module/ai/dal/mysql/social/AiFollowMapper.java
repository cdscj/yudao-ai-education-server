package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFollowDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiFollowMapper extends BaseMapperX<AiFollowDO> {

    default AiFollowDO selectByUserIdAndFollowUserId(Long userId, Long followUserId) {
        return selectOne(new LambdaQueryWrapperX<AiFollowDO>()
                .eq(AiFollowDO::getUserId, userId)
                .eq(AiFollowDO::getFollowUserId, followUserId));
    }

    default List<AiFollowDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiFollowDO>()
                .eq(AiFollowDO::getUserId, userId)
                .orderByDesc(AiFollowDO::getCreateTime));
    }

    default List<AiFollowDO> selectListByFollowUserId(Long followUserId) {
        return selectList(new LambdaQueryWrapperX<AiFollowDO>()
                .eq(AiFollowDO::getFollowUserId, followUserId)
                .orderByDesc(AiFollowDO::getCreateTime));
    }

    default void deleteByUserIdAndFollowUserId(Long userId, Long followUserId) {
        delete(new LambdaQueryWrapperX<AiFollowDO>()
                .eq(AiFollowDO::getUserId, userId)
                .eq(AiFollowDO::getFollowUserId, followUserId));
    }
}
