package cn.iocoder.yudao.module.ai.dal.mysql.school;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiSchoolDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学校信息 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiSchoolMapper extends BaseMapperX<AiSchoolDO> {

    default PageResult<AiSchoolDO> selectPage(PageParam pageParam, String name, String type, String city) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiSchoolDO>()
                .likeIfPresent(AiSchoolDO::getName, name)
                .eqIfPresent(AiSchoolDO::getType, type)
                .eqIfPresent(AiSchoolDO::getCity, city)
                .orderByDesc(AiSchoolDO::getId));
    }

    default List<AiSchoolDO> selectListByType(String type) {
        return selectList(new LambdaQueryWrapperX<AiSchoolDO>()
                .eq(AiSchoolDO::getType, type)
                .orderByDesc(AiSchoolDO::getId));
    }

    default List<AiSchoolDO> selectListByCity(String city) {
        return selectList(new LambdaQueryWrapperX<AiSchoolDO>()
                .eq(AiSchoolDO::getCity, city)
                .orderByDesc(AiSchoolDO::getId));
    }

    default AiSchoolDO selectByName(String name) {
        return selectOne(AiSchoolDO::getName, name);
    }

}
