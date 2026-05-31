package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningPathNodeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiLearningPathNodeMapper extends BaseMapperX<AiLearningPathNodeDO> {

    default List<AiLearningPathNodeDO> selectListByPathId(Long pathId) {
        return selectList(new LambdaQueryWrapperX<AiLearningPathNodeDO>()
                .eq(AiLearningPathNodeDO::getPathId, pathId)
                .orderByAsc(AiLearningPathNodeDO::getSortOrder));
    }

    default int deleteByPathId(Long pathId) {
        return delete(AiLearningPathNodeDO::getPathId, pathId);
    }
}
