package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserPointsDO;

import java.util.List;
import java.util.Map;

/**
 * AI 用户积分 Service 接口
 *
 * @author 芋道源码
 */
public interface AiUserPointsService {

    /**
     * 增加积分
     *
     * @param userId  用户编号
     * @param points  积分值
     * @param bizType 业务类型
     * @param bizId   业务编号
     * @return 更新后的积分记录
     */
    AiUserPointsDO addPoints(Long userId, Integer points, Integer bizType, Long bizId);

    /**
     * 获取用户积分
     *
     * @param userId 用户编号
     * @return 积分记录
     */
    AiUserPointsDO getUserPoints(Long userId);

    /**
     * 获取积分历史（分页）
     *
     * @param userId   用户编号
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 积分历史列表
     */
    List<AiUserPointsDO> getPointsHistory(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 获取用户等级信息
     *
     * @param userId 用户编号
     * @return 等级信息
     */
    Map<String, Object> getUserLevel(Long userId);

}
