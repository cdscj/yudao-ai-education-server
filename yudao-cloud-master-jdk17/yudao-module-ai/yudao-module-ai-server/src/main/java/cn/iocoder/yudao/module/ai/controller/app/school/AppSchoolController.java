package cn.iocoder.yudao.module.ai.controller.app.school;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiSchoolRespVO;
import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiStudentSchoolRespVO;
import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiStudentSchoolSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiSchoolDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiStudentSchoolDO;
import cn.iocoder.yudao.module.ai.service.school.AiSchoolService;
import cn.iocoder.yudao.module.ai.service.school.AiStudentSchoolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 学校信息")
@RestController
@RequestMapping("/ai/school")
@Validated
public class AppSchoolController {

    @Resource
    private AiSchoolService schoolService;
    @Resource
    private AiStudentSchoolService studentSchoolService;

    @GetMapping("/list")
    @Operation(summary = "获得学校列表")
    public CommonResult<PageResult<AiSchoolRespVO>> getSchoolList(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {
        PageResult<AiSchoolDO> pageResult = schoolService.getSchoolList(name, type, city, pageNo, pageSize);
        return success(BeanUtils.toBean(pageResult, AiSchoolRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得学校")
    @Parameter(name = "id", description = "学校编号", required = true, example = "1")
    public CommonResult<AiSchoolRespVO> getSchool(@RequestParam("id") Long id) {
        AiSchoolDO school = schoolService.getSchool(id);
        return success(BeanUtils.toBean(school, AiSchoolRespVO.class));
    }

    @GetMapping("/my-school")
    @Operation(summary = "获取我的学校信息")
    public CommonResult<AiStudentSchoolRespVO> getMySchool() {
        AiStudentSchoolDO studentSchool = studentSchoolService.getMySchool(getLoginUserId());
        if (studentSchool == null) {
            return success(null);
        }
        AiStudentSchoolRespVO respVO = BeanUtils.toBean(studentSchool, AiStudentSchoolRespVO.class);
        // 设置学校名称
        AiSchoolDO school = schoolService.getSchool(studentSchool.getSchoolId());
        if (school != null) {
            respVO.setSchoolName(school.getName());
        }
        return success(respVO);
    }

    @PostMapping("/bind")
    @Operation(summary = "绑定/更新学校信息")
    public CommonResult<Boolean> bindSchool(@RequestBody @Valid AiStudentSchoolSaveReqVO reqVO) {
        studentSchoolService.bindSchool(getLoginUserId(), reqVO);
        return success(true);
    }

    @GetMapping("/classmates")
    @Operation(summary = "获取同校同学")
    public CommonResult<List<AiStudentSchoolRespVO>> getClassmates() {
        List<AiStudentSchoolDO> list = studentSchoolService.getClassmates(getLoginUserId());
        return success(BeanUtils.toBean(list, AiStudentSchoolRespVO.class));
    }

}
