package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudyPlanDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiStudyPlanMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service @Validated
public class AiStudyPlanServiceImpl implements AiStudyPlanService {

    @Resource private AiStudyPlanMapper mapper;

    @Override public Long createPlan(AiStudyPlanDO p) {
        AiStudyPlanDO active = mapper.selectActiveByUserId(p.getUserId());
        if (active != null) throw exception(STUDY_PLAN_ALREADY_ACTIVE);
        mapper.insert(p); return p.getId();
    }

    @Override public void updatePlan(AiStudyPlanDO p) { validateExists(p.getId()); mapper.updateById(p); }

    @Override
    public void completePlan(Long id, Long userId) {
        AiStudyPlanDO p = validateExists(id);
        if (!p.getUserId().equals(userId)) throw exception(STUDY_PLAN_NOT_EXISTS);
        p.setStatus("COMPLETED"); p.setProgress(100); p.setCompletedDate(LocalDateTime.now());
        mapper.updateById(p);
    }

    @Override public AiStudyPlanDO getActivePlan(Long userId) { return mapper.selectActiveByUserId(userId); }
    @Override public AiStudyPlanDO getPlan(Long id) { return validateExists(id); }

    @Override
    public PageResult<AiStudyPlanDO> getPlanPage(Long userId, String planType, String status, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), userId, planType, status);
    }

    private AiStudyPlanDO validateExists(Long id) {
        AiStudyPlanDO p = mapper.selectById(id);
        if (p == null) throw exception(STUDY_PLAN_NOT_EXISTS);
        return p;
    }
}
