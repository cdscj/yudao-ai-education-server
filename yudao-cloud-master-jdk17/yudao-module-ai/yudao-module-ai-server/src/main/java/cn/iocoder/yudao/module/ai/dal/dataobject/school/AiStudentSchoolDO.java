package cn.iocoder.yudao.module.ai.dal.dataobject.school;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * AI 学生学校关联 DO
 *
 * @author 芋道源码
 */
@TableName("ai_student_school")
@KeySequence("ai_student_school_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiStudentSchoolDO extends TenantBaseDO {

    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 学校编号
     */
    private Long schoolId;
    /**
     * 专业
     */
    private String major;
    /**
     * 年级（如：2024级）
     */
    private String grade;
    /**
     * 班级
     */
    private String className;
    /**
     * 学号
     */
    private String studentNo;
    /**
     * 入学年份
     */
    private Integer enrollmentYear;
    /**
     * 状态
     *
     * 枚举 {@link cn.iocoder.yudao.module.ai.enums.school.AiStudentStatusEnum}
     */
    private Integer status;

}
