package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiExamDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiExamMapper extends BaseMapperX<AiExamDO> {

    default PageResult<AiExamDO> selectPage(PageParam pageParam, Long subjectId, String publishStatus, String title) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiExamDO>()
                .eqIfPresent(AiExamDO::getSubjectId, subjectId)
                .eqIfPresent(AiExamDO::getPublishStatus, publishStatus)
                .likeIfPresent(AiExamDO::getTitle, title)
                .orderByDesc(AiExamDO::getId));
    }

    default List<AiExamDO> selectPublishedList() {
        return selectList(new LambdaQueryWrapperX<AiExamDO>()
                .eq(AiExamDO::getPublishStatus, "PUBLISHED")
                .orderByDesc(AiExamDO::getId));
    }
}
