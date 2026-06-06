package cn.iocoder.yudao.module.ai.service.school;

import cn.iocoder.yudao.module.ai.controller.admin.school.vo.AiScheduleImportReqVO;
import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiCourseSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiCourseScheduleDO;

import java.util.List;
import java.util.Map;

/**
 * AI 课程表 Service 接口
 *
 * @author 芋道源码
 */
public interface AiCourseScheduleService {

    /**
     * 获得周课程表（按星期分组）—— 支持按用户或学校查询
     *
     * @param userId   用户编号
     * @param schoolId 学校编号
     * @param semester 学期
     * @return 按星期分组的课程表
     */
    Map<Integer, List<AiCourseScheduleDO>> getWeeklySchedule(Long userId, Long schoolId, String semester);

    /**
     * 管理员批量导入课程（关联学校）
     *
     * @param reqVO 导入请求
     * @return 导入数量
     */
    int importCourses(AiScheduleImportReqVO reqVO);

    /**
     * 添加课程
     *
     * @param userId 用户编号
     * @param reqVO  课程信息
     * @return 课程编号
     */
    Long addCourse(Long userId, AiCourseSaveReqVO reqVO);

    /**
     * 更新课程
     *
     * @param userId 用户编号
     * @param reqVO  课程信息
     */
    void updateCourse(Long userId, AiCourseSaveReqVO reqVO);

    /**
     * 删除课程
     *
     * @param id     课程编号
     * @param userId 用户编号
     */
    void deleteCourse(Long id, Long userId);

    /**
     * 获得课程
     *
     * @param id 课程编号
     * @return 课程
     */
    AiCourseScheduleDO getCourse(Long id);

    /**
     * 获得今日课程 —— 支持按用户或学校查询
     *
     * @param userId   用户编号
     * @param schoolId 学校编号
     * @return 课程列表
     */
    List<AiCourseScheduleDO> getTodayCourses(Long userId, Long schoolId);

}
