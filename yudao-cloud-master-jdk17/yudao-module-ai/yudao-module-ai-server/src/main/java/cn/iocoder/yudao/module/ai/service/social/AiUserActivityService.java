package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserActivityDO;

import java.util.List;

/**
 * AI 用户动态 Service 接口
 *
 * @author 芋道源码
 */
public interface AiUserActivityService {

    /**
     * 发布动态
     *
     * @param userId       用户编号
     * @param activityType 动态类型
     * @param content      动态内容
     * @param refId        关联编号
     */
    void publishActivity(Long userId, Integer activityType, String content, Long refId);

    /**
     * 获取动态 Feed 流
     *
     * @param userId   用户编号
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 动态分页
     */
    PageResult<AiUserActivityDO> getActivityFeed(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 获取用户动态列表
     *
     * @param userId   用户编号
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 动态列表
     */
    List<AiUserActivityDO> getUserActivities(Long userId, Integer pageNo, Integer pageSize);

}
