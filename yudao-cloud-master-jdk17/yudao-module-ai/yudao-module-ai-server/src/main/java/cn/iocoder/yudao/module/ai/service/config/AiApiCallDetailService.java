package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics.AiApiCallDetailPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiCallDetailDO;

/**
 * AI API 调用详情 Service 接口
 *
 * @author yudao
 */
public interface AiApiCallDetailService {

    /**
     * 创建调用详情记录
     *
     * @param detail 调用详情
     */
    void createCallDetail(AiApiCallDetailDO detail);

    /**
     * 获得调用详情分页
     *
     * @param pageReqVO 分页查询
     * @return 分页结果
     */
    PageResult<AiApiCallDetailDO> getCallDetailPage(AiApiCallDetailPageReqVO pageReqVO);
}
