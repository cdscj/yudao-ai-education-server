package cn.iocoder.yudao.module.ai.dal.mysql.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.userquota.AiUserQuotaPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiUserQuotaDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * AI 用户配额 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiUserQuotaMapper extends BaseMapperX<AiUserQuotaDO> {

    default AiUserQuotaDO selectByUserId(Long userId) {
        return selectOne(AiUserQuotaDO::getUserId, userId);
    }

    default PageResult<AiUserQuotaDO> selectPage(AiUserQuotaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiUserQuotaDO>()
                .eqIfPresent(AiUserQuotaDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiUserQuotaDO::getQuotaType, reqVO.getQuotaType())
                .eqIfPresent(AiUserQuotaDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiUserQuotaDO::getId));
    }

    /**
     * 更新已使用的配额（自增）
     */
    @Update("UPDATE ai_user_quota SET daily_used = daily_used + #{delta}, " +
            "monthly_used = monthly_used + #{delta}, total_used = total_used + #{delta} " +
            "WHERE id = #{id}")
    int updateUsedQuota(@Param("id") Long id, @Param("delta") Integer delta);

    /**
     * 重置每日已用配额
     */
    @Update("UPDATE ai_user_quota SET daily_used = 0")
    void resetDailyUsed();

    /**
     * 重置每月已用配额
     */
    @Update("UPDATE ai_user_quota SET monthly_used = 0")
    void resetMonthlyUsed();

}
