package cn.iocoder.yudao.module.ai.controller.admin.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.StudentProfileChatReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.StudentProfileRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudentProfileDO;
import cn.iocoder.yudao.module.ai.service.education.AiStudentProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "管理后台 - 学生画像")
@RestController
@RequestMapping("/ai/education/profile")
@Validated
public class AiStudentProfileController {

    @Resource
    private AiStudentProfileService studentProfileService;

    @GetMapping("/get")
    @Operation(summary = "获取当前用户画像")
    @PreAuthorize("@ss.hasPermission('ai:student-profile:query')")
    public CommonResult<StudentProfileRespVO> getProfile() {
        AiStudentProfileDO profile = studentProfileService.getProfileByUserId(getLoginUserId());
        return success(BeanUtils.toBean(profile, StudentProfileRespVO.class));
    }

    @PostMapping(value = "/chat-build", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "对话式构建画像（流式）")
    @PreAuthorize("@ss.hasPermission('ai:student-profile:query')")
    public Flux<CommonResult<String>> chatBuildProfile(@RequestBody @Valid StudentProfileChatReqVO chatReqVO) {
        return studentProfileService.chatBuildProfile(chatReqVO, getLoginUserId());
    }
}
