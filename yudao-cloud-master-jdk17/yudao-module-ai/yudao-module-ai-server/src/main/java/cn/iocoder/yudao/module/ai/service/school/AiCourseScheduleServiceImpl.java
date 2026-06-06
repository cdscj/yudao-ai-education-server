package cn.iocoder.yudao.module.ai.service.school;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiScheduleImportReqVO;
import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiCourseSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiCourseScheduleDO;
import cn.iocoder.yudao.module.ai.dal.mysql.school.AiCourseScheduleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.COURSE_NOT_EXISTS;

/**
 * AI 课程表 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiCourseScheduleServiceImpl implements AiCourseScheduleService {

    @Resource
    private AiCourseScheduleMapper courseScheduleMapper;

    @Override
    public Map<Integer, List<AiCourseScheduleDO>> getWeeklySchedule(Long userId, Long schoolId, String semester) {
        List<AiCourseScheduleDO> courseList = new ArrayList<>();

        // 1. 查询学校导入的课程（schoolId + semester）
        if (schoolId != null && semester != null) {
            courseList.addAll(courseScheduleMapper.selectListBySchoolIdAndSemester(schoolId, semester));
        }

        // 2. 查询用户自己添加的课程（userId + semester）
        if (userId != null) {
            List<AiCourseScheduleDO> userCourses;
            if (semester != null) {
                userCourses = courseScheduleMapper.selectListByUserIdAndSemester(userId, semester);
            } else {
                userCourses = courseScheduleMapper.selectListByUserId(userId);
            }
            courseList.addAll(userCourses);
        }

        if (CollUtil.isEmpty(courseList)) {
            return Collections.emptyMap();
        }

        // 按 dayOfWeek 分组
        Map<Integer, List<AiCourseScheduleDO>> weeklyMap = new HashMap<>();
        for (AiCourseScheduleDO course : courseList) {
            weeklyMap.computeIfAbsent(course.getDayOfWeek(), k -> new ArrayList<>())
                    .add(course);
        }
        return weeklyMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int importCourses(AiScheduleImportReqVO reqVO) {
        List<AiCourseScheduleDO> courses = BeanUtils.toBean(reqVO.getCourses(), AiCourseScheduleDO.class);
        int count = 0;
        for (AiCourseScheduleDO course : courses) {
            course.setUserId(null); // 管理员导入，不绑定具体用户
            course.setSchoolId(reqVO.getSchoolId());
            course.setSemester(reqVO.getSemester());
            courseScheduleMapper.insert(course);
            count++;
        }
        return count;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long addCourse(Long userId, AiCourseSaveReqVO reqVO) {
        AiCourseScheduleDO course = BeanUtils.toBean(reqVO, AiCourseScheduleDO.class);
        course.setUserId(userId);
        courseScheduleMapper.insert(course);
        return course.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCourse(Long userId, AiCourseSaveReqVO reqVO) {
        AiCourseScheduleDO existing = courseScheduleMapper.selectById(reqVO.getId());
        if (existing == null) {
            throw exception(COURSE_NOT_EXISTS);
        }
        AiCourseScheduleDO updateObj = BeanUtils.toBean(reqVO, AiCourseScheduleDO.class);
        updateObj.setUserId(userId);
        courseScheduleMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Long id, Long userId) {
        AiCourseScheduleDO existing = courseScheduleMapper.selectById(id);
        if (existing == null) {
            throw exception(COURSE_NOT_EXISTS);
        }
        courseScheduleMapper.deleteById(id);
    }

    @Override
    public AiCourseScheduleDO getCourse(Long id) {
        return courseScheduleMapper.selectById(id);
    }

    @Override
    public List<AiCourseScheduleDO> getTodayCourses(Long userId, Long schoolId) {
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        int dayValue = dayOfWeek.getValue();
        List<AiCourseScheduleDO> result = new ArrayList<>();

        // 1. 学校课程
        if (schoolId != null) {
            result.addAll(courseScheduleMapper.selectListBySchoolIdAndDay(schoolId, dayValue));
        }
        // 2. 用户个人课程
        if (userId != null) {
            result.addAll(courseScheduleMapper.selectListByUserIdAndDay(userId, dayValue));
        }

        return result;
    }

}
