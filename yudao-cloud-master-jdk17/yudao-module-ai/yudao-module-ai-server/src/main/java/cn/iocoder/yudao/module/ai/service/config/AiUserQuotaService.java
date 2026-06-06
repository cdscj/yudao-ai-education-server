package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.AiUserQuotaBatchSaveReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.AiUserQuotaPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.AiUserQuotaSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiUserQuotaDO;
import jakarta.validation.Valid;

/**
 * AI 用户配额 Service 接口
 *
 * @author 芋道源码
 */
public interface AiUserQuotaService {

    /**
     * 创建用户配额
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createUserQuota(@Valid AiUserQuotaSaveReqVO createReqVO);

    /**
     * 更新用户配额
     *
     * @param updateReqVO 更新信息
     */
    void updateUserQuota(@Valid AiUserQuotaSaveReqVO updateReqVO);

    /**
     * 删除用户配额
     *
     * @param id 编号
     */
    void deleteUserQuota(Long id);

    /**
     * 获得用户配额
     *
     * @param id 编号
     * @return 用户配额
     */
    AiUserQuotaDO getUserQuota(Long id);

    /**
     * 获得用户配额（根据用户编号）
     *
     * @param userId 用户编号
     * @return 用户配额
     */
    AiUserQuotaDO getUserQuotaByUserId(Long userId);

    /**
     * 获得用户配额分页
     *
     * @param pageReqVO 分页查询
     * @return 用户配额分页
     */
    PageResult<AiUserQuotaDO> getUserQuotaPage(AiUserQuotaPageReqVO pageReqVO);

    /**
     * 批量设置用户配额
     *
     * @param batchSaveReqVO 批量设置信息
     */
    void batchSetUserQuota(AiUserQuotaBatchSaveReqVO batchSaveReqVO);

    /**
     * 检查并扣除配额
     *
     * @param userId 用户编号
     * @param tokens 需要扣除的 Token 数
     * @return 是否允许通过（true=配额充足，已扣除；false=配额不足或已禁用）
     */
    boolean checkAndDeductQuota(Long userId, Integer tokens);

    /**
     * 重置每日配额（定时任务使用）
     */
    void resetDailyQuotas();

    /**
     * 重置每月配额（定时任务使用）
     */
    void resetMonthlyQuotas();

}
