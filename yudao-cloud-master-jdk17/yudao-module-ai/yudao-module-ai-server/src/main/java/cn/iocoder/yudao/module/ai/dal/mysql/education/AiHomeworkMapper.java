package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiHomeworkDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiHomeworkMapper extends BaseMapperX<AiHomeworkDO> {

    default PageResult<AiHomeworkDO> selectPage(PageParam pageParam, Long subjectId, String publishStatus, String title) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiHomeworkDO>()
                .eqIfPresent(AiHomeworkDO::getSubjectId, subjectId)
                .eqIfPresent(AiHomeworkDO::getPublishStatus, publishStatus)
                .likeIfPresent(AiHomeworkDO::getTitle, title)
                .orderByDesc(AiHomeworkDO::getId));
    }

    default List<AiHomeworkDO> selectPublishedList(Long subjectId) {
        return selectList(new LambdaQueryWrapperX<AiHomeworkDO>()
                .eqIfPresent(AiHomeworkDO::getSubjectId, subjectId)
                .eq(AiHomeworkDO::getPublishStatus, "PUBLISHED")
                .orderByDesc(AiHomeworkDO::getId));
    }
}
