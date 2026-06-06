package cn.iocoder.yudao.module.ai.dal.mysql.school;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiStudentSchoolDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 学生学校关联 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiStudentSchoolMapper extends BaseMapperX<AiStudentSchoolDO> {

    default AiStudentSchoolDO selectByUserId(Long userId) {
        return selectOne(AiStudentSchoolDO::getUserId, userId);
    }

    default List<AiStudentSchoolDO> selectListBySchoolId(Long schoolId) {
        return selectList(new LambdaQueryWrapperX<AiStudentSchoolDO>()
                .eq(AiStudentSchoolDO::getSchoolId, schoolId)
                .orderByDesc(AiStudentSchoolDO::getId));
    }

    default List<AiStudentSchoolDO> selectListBySchoolIdAndGrade(Long schoolId, String grade) {
        return selectList(new LambdaQueryWrapperX<AiStudentSchoolDO>()
                .eq(AiStudentSchoolDO::getSchoolId, schoolId)
                .eq(AiStudentSchoolDO::getGrade, grade)
                .orderByDesc(AiStudentSchoolDO::getId));
    }

}
