package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics.AiApiCallDetailPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiCallDetailDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiApiCallDetailMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * AI API 调用详情 Service 实现类
 *
 * @author yudao
 */
@Service
@Slf4j
public class AiApiCallDetailServiceImpl implements AiApiCallDetailService {

    @Resource
    private AiApiCallDetailMapper callDetailMapper;

    @Override
    @Async
    public void createCallDetail(AiApiCallDetailDO detail) {
        try {
            callDetailMapper.insert(detail);
        } catch (Exception e) {
            log.warn("[createCallDetail] 写入调用详情失败，不影响主流程: {}", e.getMessage());
        }
    }

    @Override
    public PageResult<AiApiCallDetailDO> getCallDetailPage(AiApiCallDetailPageReqVO pageReqVO) {
        return callDetailMapper.selectPage(pageReqVO);
    }
}
