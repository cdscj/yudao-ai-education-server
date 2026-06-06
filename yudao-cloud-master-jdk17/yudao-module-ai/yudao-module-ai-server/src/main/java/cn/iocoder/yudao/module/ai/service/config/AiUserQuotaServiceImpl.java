package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.AiUserQuotaBatchSaveReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.AiUserQuotaPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.AiUserQuotaSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiUserQuotaDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiUserQuotaMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI 用户配额 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class AiUserQuotaServiceImpl implements AiUserQuotaService {

    @Resource
    private AiUserQuotaMapper userQuotaMapper;

    @Override
    public Long createUserQuota(AiUserQuotaSaveReqVO createReqVO) {
        // 插入
        AiUserQuotaDO quota = BeanUtils.toBean(createReqVO, AiUserQuotaDO.class);
        userQuotaMapper.insert(quota);
        return quota.getId();
    }

    @Override
    public void updateUserQuota(AiUserQuotaSaveReqVO updateReqVO) {
        // 校验存在
        validateUserQuotaExists(updateReqVO.getId());
        // 更新
        AiUserQuotaDO updateObj = BeanUtils.toBean(updateReqVO, AiUserQuotaDO.class);
        userQuotaMapper.updateById(updateObj);
    }

    @Override
    public void deleteUserQuota(Long id) {
        // 校验存在
        validateUserQuotaExists(id);
        // 删除
        userQuotaMapper.deleteById(id);
    }

    private AiUserQuotaDO validateUserQuotaExists(Long id) {
        AiUserQuotaDO quota = userQuotaMapper.selectById(id);
        if (quota == null) {
            throw exception(USER_QUOTA_NOT_EXISTS);
        }
        return quota;
    }

    @Override
    public AiUserQuotaDO getUserQuota(Long id) {
        return userQuotaMapper.selectById(id);
    }

    @Override
    public AiUserQuotaDO getUserQuotaByUserId(Long userId) {
        return userQuotaMapper.selectByUserId(userId);
    }

    @Override
    public PageResult<AiUserQuotaDO> getUserQuotaPage(AiUserQuotaPageReqVO pageReqVO) {
        return userQuotaMapper.selectPage(pageReqVO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndDeductQuota(Long userId, Integer tokens) {
        // 1. 获取用户配额
        AiUserQuotaDO quota = userQuotaMapper.selectByUserId(userId);
        // 如果未配置配额，默认不限制
        if (quota == null) {
            return true;
        }

        // 2. 校验状态
        if (CommonStatusEnum.isDisable(quota.getStatus())) {
            throw exception(USER_QUOTA_DISABLED);
        }

        // 3. 校验配额限制
        if (quota.getDailyLimit() != null && quota.getDailyUsed() != null
                && quota.getDailyUsed() + tokens > quota.getDailyLimit()) {
            throw exception(USER_QUOTA_DAILY_EXCEEDED);
        }
        if (quota.getMonthlyLimit() != null && quota.getMonthlyUsed() != null
                && quota.getMonthlyUsed() + tokens > quota.getMonthlyLimit()) {
            throw exception(USER_QUOTA_MONTHLY_EXCEEDED);
        }
        if (quota.getTotalLimit() != null && quota.getTotalUsed() != null
                && quota.getTotalUsed() + tokens > quota.getTotalLimit()) {
            throw exception(USER_QUOTA_TOTAL_EXCEEDED);
        }

        // 4. 扣除配额（使用 SQL 自增）
        int updated = userQuotaMapper.updateUsedQuota(quota.getId(), tokens);
        return updated > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetDailyQuotas() {
        userQuotaMapper.resetDailyUsed();
        log.info("[resetDailyQuotas][成功重置所有用户的每日配额]");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetMonthlyQuotas() {
        userQuotaMapper.resetMonthlyUsed();
        log.info("[resetMonthlyQuotas][成功重置所有用户的每月配额]");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSetUserQuota(AiUserQuotaBatchSaveReqVO batchSaveReqVO) {
        Integer dailyLimit = batchSaveReqVO.getDailyLimit() != null ? batchSaveReqVO.getDailyLimit().intValue() : null;
        Integer monthlyLimit = batchSaveReqVO.getMonthlyLimit() != null ? batchSaveReqVO.getMonthlyLimit().intValue() : null;
        Integer totalLimit = batchSaveReqVO.getTotalLimit() != null ? batchSaveReqVO.getTotalLimit().intValue() : null;
        for (Long userId : batchSaveReqVO.getUserIds()) {
            AiUserQuotaDO quota = userQuotaMapper.selectByUserId(userId);
            if (quota != null) {
                // 更新已有配额
                if (dailyLimit != null) {
                    quota.setDailyLimit(dailyLimit);
                }
                if (monthlyLimit != null) {
                    quota.setMonthlyLimit(monthlyLimit);
                }
                if (totalLimit != null) {
                    quota.setTotalLimit(totalLimit);
                }
                userQuotaMapper.updateById(quota);
            } else {
                // 创建新配额
                AiUserQuotaDO newQuota = AiUserQuotaDO.builder()
                        .userId(userId)
                        .dailyLimit(dailyLimit)
                        .monthlyLimit(monthlyLimit)
                        .totalLimit(totalLimit)
                        .dailyUsed(0)
                        .monthlyUsed(0)
                        .totalUsed(0)
                        .build();
                userQuotaMapper.insert(newQuota);
            }
        }
    }

}
