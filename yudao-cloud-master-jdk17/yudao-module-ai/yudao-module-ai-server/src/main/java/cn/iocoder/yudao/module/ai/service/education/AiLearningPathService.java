package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningPathDO;
import reactor.core.publisher.Flux;

public interface AiLearningPathService {

    Flux<CommonResult<String>> generatePath(LearningPathGenerateReqVO reqVO, Long userId);

    AiLearningPathDO getPath(Long id);

    PageResult<AiLearningPathDO> getPathPage(LearningPathPageReqVO reqVO);

    void updateNodeStatus(Long nodeId, String status);

    void deletePath(Long id);

}
