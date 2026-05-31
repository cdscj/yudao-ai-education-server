package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudentProfileDO;
import reactor.core.publisher.Flux;

public interface AiStudentProfileService {

    AiStudentProfileDO getProfileByUserId(Long userId);

    Flux<CommonResult<String>> chatBuildProfile(StudentProfileChatReqVO chatReqVO, Long userId);

    AiStudentProfileDO updateProfile(Long id, AiStudentProfileDO profile);

}
