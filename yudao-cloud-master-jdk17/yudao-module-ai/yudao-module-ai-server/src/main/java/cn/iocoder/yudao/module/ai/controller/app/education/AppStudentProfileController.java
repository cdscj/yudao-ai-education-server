package cn.iocoder.yudao.module.ai.controller.app.education;

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
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 学生画像")
@RestController
@RequestMapping("/ai/education/profile")
public class AppStudentProfileController {

    @Resource
    private AiStudentProfileService studentProfileService;

    @GetMapping("/get")
    @Operation(summary = "获取画像")
    public CommonResult<StudentProfileRespVO> getProfile() {
        AiStudentProfileDO profile = studentProfileService.getProfileByUserId(getLoginUserId());
        return success(BeanUtils.toBean(profile, StudentProfileRespVO.class));
    }

    @PostMapping(value = "/chat-build", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "对话式构建画像")
    public Flux<CommonResult<String>> chatBuildProfile(@RequestBody @Valid StudentProfileChatReqVO reqVO) {
        return studentProfileService.chatBuildProfile(reqVO, getLoginUserId());
    }
}
