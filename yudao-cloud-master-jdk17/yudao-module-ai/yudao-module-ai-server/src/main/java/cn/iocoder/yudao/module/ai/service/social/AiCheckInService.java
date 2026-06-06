package cn.iocoder.yudao.module.ai.service.social;

import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiCheckInRecordDO;

import java.util.List;
import java.util.Map;

/**
 * AI 签到 Service 接口
 *
 * @author 芋道源码
 */
public interface AiCheckInService {

    /**
     * 用户签到
     *
     * @param userId 用户编号
     * @return 签到记录
     */
    AiCheckInRecordDO checkIn(Long userId);

    /**
     * 获取签到摘要
     *
     * @param userId 用户编号
     * @return 签到摘要（总天数、连续天数、今日是否已签到）
     */
    Map<String, Object> getCheckInSummary(Long userId);

    /**
     * 获取签到记录（分页）
     *
     * @param userId   用户编号
     * @param pageNo   页码
     * @param pageSize 每页条数
     * @return 签到记录列表
     */
    List<AiCheckInRecordDO> getCheckInRecords(Long userId, Integer pageNo, Integer pageSize);

    /**
     * 获取签到日历
     *
     * @param userId    用户编号
     * @param yearMonth 年月（如：2026-06）
     * @return 签到记录列表
     */
    List<AiCheckInRecordDO> getCheckInCalendar(Long userId, String yearMonth);

}
