package cn.iocoder.yudao.module.ai.dal.dataobject.school;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI 课程表 DO
 *
 * @author 芋道源码
 */
@TableName("ai_course_schedule")
@KeySequence("ai_course_schedule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiCourseScheduleDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号（管理员导入时可为 NULL）
     */
    private Long userId;
    /**
     * 学校编号（管理员批量导入时使用）
     */
    private Long schoolId;
    /**
     * 课程名称
     */
    private String courseName;
    /**
     * 授课教师
     */
    private String teacher;
    /**
     * 上课地点
     */
    private String classroom;
    /**
     * 星期（1-7，1=周一）
     */
    private Integer dayOfWeek;
    /**
     * 开始时间（HH:mm）
     */
    private String startTime;
    /**
     * 结束时间（HH:mm）
     */
    private String endTime;
    /**
     * 开始节次
     */
    private Integer startPeriod;
    /**
     * 结束节次
     */
    private Integer endPeriod;
    /**
     * 课程颜色
     */
    private String color;
    /**
     * 课程类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.ai.enums.school.AiCourseTypeEnum}
     */
    private String courseType;
    /**
     * 学期（如：2024-2025-1）
     */
    private String semester;
    /**
     * 周类型
     *
     * 枚举 {@link cn.iocoder.yudao.module.ai.enums.school.AiWeekTypeEnum}
     */
    private String weekType;
    /**
     * 备注
     */
    private String remark;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;

}
