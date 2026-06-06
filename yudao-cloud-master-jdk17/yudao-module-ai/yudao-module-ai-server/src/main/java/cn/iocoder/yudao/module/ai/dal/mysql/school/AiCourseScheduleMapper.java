package cn.iocoder.yudao.module.ai.dal.mysql.school;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiCourseScheduleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 课程表 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiCourseScheduleMapper extends BaseMapperX<AiCourseScheduleDO> {

    default List<AiCourseScheduleDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiCourseScheduleDO>()
                .eq(AiCourseScheduleDO::getUserId, userId)
                .orderByAsc(AiCourseScheduleDO::getDayOfWeek)
                .orderByAsc(AiCourseScheduleDO::getStartTime));
    }

    default List<AiCourseScheduleDO> selectListByUserIdAndDay(Long userId, Integer dayOfWeek) {
        return selectList(new LambdaQueryWrapperX<AiCourseScheduleDO>()
                .eq(AiCourseScheduleDO::getUserId, userId)
                .eq(AiCourseScheduleDO::getDayOfWeek, dayOfWeek)
                .orderByAsc(AiCourseScheduleDO::getStartTime));
    }

    default List<AiCourseScheduleDO> selectListByUserIdAndSemester(Long userId, String semester) {
        return selectList(new LambdaQueryWrapperX<AiCourseScheduleDO>()
                .eq(AiCourseScheduleDO::getUserId, userId)
                .eq(AiCourseScheduleDO::getSemester, semester)
                .orderByAsc(AiCourseScheduleDO::getDayOfWeek)
                .orderByAsc(AiCourseScheduleDO::getStartTime));
    }

    default List<AiCourseScheduleDO> selectListBySchoolIdAndSemester(Long schoolId, String semester) {
        return selectList(new LambdaQueryWrapperX<AiCourseScheduleDO>()
                .eq(AiCourseScheduleDO::getSchoolId, schoolId)
                .eq(AiCourseScheduleDO::getSemester, semester)
                .orderByAsc(AiCourseScheduleDO::getDayOfWeek)
                .orderByAsc(AiCourseScheduleDO::getStartTime));
    }

    default List<AiCourseScheduleDO> selectListBySchoolIdAndDay(Long schoolId, Integer dayOfWeek) {
        return selectList(new LambdaQueryWrapperX<AiCourseScheduleDO>()
                .eq(AiCourseScheduleDO::getSchoolId, schoolId)
                .eq(AiCourseScheduleDO::getDayOfWeek, dayOfWeek)
                .orderByAsc(AiCourseScheduleDO::getStartTime));
    }

    default PageResult<AiCourseScheduleDO> selectPage(PageParam pageParam, Long userId, String courseName,
                                                      Integer dayOfWeek, String semester) {
        return selectPage(pageParam, new LambdaQueryWrapperX<AiCourseScheduleDO>()
                .eqIfPresent(AiCourseScheduleDO::getUserId, userId)
                .likeIfPresent(AiCourseScheduleDO::getCourseName, courseName)
                .eqIfPresent(AiCourseScheduleDO::getDayOfWeek, dayOfWeek)
                .eqIfPresent(AiCourseScheduleDO::getSemester, semester)
                .orderByAsc(AiCourseScheduleDO::getDayOfWeek)
                .orderByAsc(AiCourseScheduleDO::getStartTime));
    }

}
