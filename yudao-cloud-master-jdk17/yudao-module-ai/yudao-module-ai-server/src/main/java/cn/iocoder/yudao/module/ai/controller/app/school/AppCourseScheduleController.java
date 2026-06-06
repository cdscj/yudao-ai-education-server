package cn.iocoder.yudao.module.ai.controller.app.school;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiCourseRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiCourseScheduleDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiStudentSchoolDO;
import cn.iocoder.yudao.module.ai.service.school.AiCourseScheduleService;
import cn.iocoder.yudao.module.ai.service.school.AiStudentSchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 课程表")
@RestController
@RequestMapping("/ai/schedule")
@Validated
public class AppCourseScheduleController {

    @Resource
    private AiCourseScheduleService courseScheduleService;

    @Resource
    private AiStudentSchoolService studentSchoolService;

    @GetMapping("/weekly")
    @Operation(summary = "获得周课程表（优先显示学校导入的课表）")
    @Parameter(name = "semester", description = "学期", example = "2024-2025-1")
    public CommonResult<Map<Integer, List<AiCourseRespVO>>> getWeeklySchedule(
            @RequestParam(value = "semester", required = false) String semester) {
        Long userId = getLoginUserId();
        // 查询学生所属学校
        Long schoolId = getStudentSchoolId(userId);

        Map<Integer, List<AiCourseScheduleDO>> weeklyMap = courseScheduleService.getWeeklySchedule(userId, schoolId, semester);
        // 转换 DO 为 VO
        Map<Integer, List<AiCourseRespVO>> result = new java.util.HashMap<>();
        for (Map.Entry<Integer, List<AiCourseScheduleDO>> entry : weeklyMap.entrySet()) {
            result.put(entry.getKey(), BeanUtils.toBean(entry.getValue(), AiCourseRespVO.class));
        }
        return success(result);
    }

    @GetMapping("/today")
    @Operation(summary = "获得今日课程（优先显示学校导入的课表）")
    public CommonResult<List<AiCourseRespVO>> getTodayCourses() {
        Long userId = getLoginUserId();
        Long schoolId = getStudentSchoolId(userId);
        List<AiCourseScheduleDO> list = courseScheduleService.getTodayCourses(userId, schoolId);
        return success(BeanUtils.toBean(list, AiCourseRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程")
    @Parameter(name = "id", description = "课程编号", required = true, example = "1")
    public CommonResult<AiCourseRespVO> getCourse(@RequestParam("id") Long id) {
        AiCourseScheduleDO course = courseScheduleService.getCourse(id);
        return success(BeanUtils.toBean(course, AiCourseRespVO.class));
    }

    /**
     * 获取当前登录学生所属学校编号（未绑定则返回 null）
     */
    private Long getStudentSchoolId(Long userId) {
        try {
            AiStudentSchoolDO studentSchool = studentSchoolService.getMySchool(userId);
            return studentSchool != null ? studentSchool.getSchoolId() : null;
        } catch (Exception e) {
            return null;
        }
    }

}
