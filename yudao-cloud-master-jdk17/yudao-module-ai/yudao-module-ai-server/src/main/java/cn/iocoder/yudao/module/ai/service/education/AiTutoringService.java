package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringChatReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringMessageRespVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.TutoringSessionRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiTutoringSessionDO;
import reactor.core.publisher.Flux;

import java.util.List;

public interface AiTutoringService {

    Flux<CommonResult<String>> chat(TutoringChatReqVO reqVO, Long userId);

    List<TutoringSessionRespVO> getSessions(Long userId);

    List<TutoringMessageRespVO> getMessages(Long sessionId);

    void deleteSession(Long id);

}
