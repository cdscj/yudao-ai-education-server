package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiKnowledgeTagMapper extends BaseMapperX<AiKnowledgeTagDO> {

    default PageResult<AiKnowledgeTagDO> selectPage(PageParam pageParam, Long subjectId, String name, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiKnowledgeTagDO>()
                .eqIfPresent(AiKnowledgeTagDO::getSubjectId, subjectId)
                .likeIfPresent(AiKnowledgeTagDO::getName, name)
                .eqIfPresent(AiKnowledgeTagDO::getStatus, status)
                .orderByAsc(AiKnowledgeTagDO::getSort));
    }

    default List<AiKnowledgeTagDO> selectListBySubjectId(Long subjectId) {
        return selectList(new LambdaQueryWrapperX<AiKnowledgeTagDO>()
                .eq(AiKnowledgeTagDO::getSubjectId, subjectId)
                .eq(AiKnowledgeTagDO::getStatus, 0)
                .orderByAsc(AiKnowledgeTagDO::getSort));
    }
}
