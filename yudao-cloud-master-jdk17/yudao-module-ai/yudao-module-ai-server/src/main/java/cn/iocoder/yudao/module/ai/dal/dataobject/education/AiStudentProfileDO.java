package cn.iocoder.yudao.module.ai.dal.dataobject.education;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "ai_student_profile")
@KeySequence("ai_student_profile_seq")
@Data
public class AiStudentProfileDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String major;
    private String grade;
    private String learningGoals;
    private String knowledgeLevel;
    private String learningPreferences;
    private String weakPoints;
    private String strongPoints;
    private String studyHistory;
    private Integer learningSpeed;
    private String preferredResourceTypes;
    private String studyTimePreference;
    private String status;
    private String profileJson;

}
