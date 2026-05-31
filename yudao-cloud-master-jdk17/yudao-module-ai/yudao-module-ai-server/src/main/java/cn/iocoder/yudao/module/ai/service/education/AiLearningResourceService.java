package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningResourceDO;
import reactor.core.publisher.Flux;

public interface AiLearningResourceService {

    Flux<CommonResult<String>> generateResource(LearningResourceGenerateReqVO reqVO, Long userId);

    AiLearningResourceDO getResource(Long id);

    PageResult<AiLearningResourceDO> getResourcePage(LearningResourcePageReqVO reqVO);

    void deleteResource(Long id);

}
