package cn.iocoder.yudao.module.ai.dal.mysql.social;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLearningGoalDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiLearningGoalMapper extends BaseMapperX<AiLearningGoalDO> {

    default List<AiLearningGoalDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiLearningGoalDO>()
                .eq(AiLearningGoalDO::getUserId, userId)
                .orderByDesc(AiLearningGoalDO::getCreateTime));
    }

    default long selectCountByUserIdAndStatus(Long userId, String status) {
        return selectCount(new LambdaQueryWrapperX<AiLearningGoalDO>()
                .eq(AiLearningGoalDO::getUserId, userId)
                .eq(AiLearningGoalDO::getStatus, status));
    }
}
