package cn.iocoder.yudao.module.ai.service.chat;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.chat.vo.log.AiConversationLogPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.chat.AiConversationLogDO;

/**
 * AI 对话日志 Service 接口
 *
 * @author 芋道源码
 */
public interface AiConversationLogService {

    /**
     * 创建对话日志
     *
     * @param log 对话日志
     */
    void createConversationLog(AiConversationLogDO log);

    /**
     * 获得对话日志分页
     *
     * @param pageReqVO 分页查询
     * @return 对话日志分页
     */
    PageResult<AiConversationLogDO> getConversationLogPage(AiConversationLogPageReqVO pageReqVO);

    /**
     * 获得对话日志
     *
     * @param id 编号
     * @return 对话日志
     */
    AiConversationLogDO getConversationLog(Long id);

    /**
     * 获得对话日志数量
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 对话日志数量
     */
    Long getConversationLogCount(String startDate, String endDate);

    /**
     * 删除对话日志
     *
     * @param id 编号
     */
    void deleteConversationLog(Long id);

}
