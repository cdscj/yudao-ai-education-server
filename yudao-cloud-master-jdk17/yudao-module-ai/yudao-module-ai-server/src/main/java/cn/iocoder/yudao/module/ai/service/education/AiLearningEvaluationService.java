package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningEvaluationDO;
import reactor.core.publisher.Flux;

public interface AiLearningEvaluationService {

    Flux<CommonResult<String>> generateEvaluation(Long userId);

    PageResult<AiLearningEvaluationDO> getEvaluationPage(EvaluationPageReqVO reqVO);

}
