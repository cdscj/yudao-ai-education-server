package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiSubjectCategoryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiSubjectCategoryMapper extends BaseMapperX<AiSubjectCategoryDO> {

    default PageResult<AiSubjectCategoryDO> selectPage(PageParam pageParam, String name, Integer status) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiSubjectCategoryDO>()
                .likeIfPresent(AiSubjectCategoryDO::getName, name)
                .eqIfPresent(AiSubjectCategoryDO::getStatus, status)
                .orderByAsc(AiSubjectCategoryDO::getSort));
    }

    default AiSubjectCategoryDO selectByCode(String code) {
        return selectOne(AiSubjectCategoryDO::getCode, code);
    }

    default List<AiSubjectCategoryDO> selectListByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<AiSubjectCategoryDO>()
                .eq(AiSubjectCategoryDO::getParentId, parentId)
                .eq(AiSubjectCategoryDO::getStatus, 0)
                .orderByAsc(AiSubjectCategoryDO::getSort));
    }

    default List<AiSubjectCategoryDO> selectListByStatus(Integer status) {
        return selectList(new LambdaQueryWrapperX<AiSubjectCategoryDO>()
                .eqIfPresent(AiSubjectCategoryDO::getStatus, status)
                .orderByAsc(AiSubjectCategoryDO::getSort));
    }
}
