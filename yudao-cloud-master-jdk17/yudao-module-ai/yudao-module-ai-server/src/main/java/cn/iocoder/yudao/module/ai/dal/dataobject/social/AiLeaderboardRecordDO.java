package cn.iocoder.yudao.module.ai.dal.dataobject.social;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;

@TableName("ai_leaderboard_record")
@KeySequence("ai_leaderboard_record_seq")
@Data
public class AiLeaderboardRecordDO extends TenantBaseDO {

    @TableId
    private Long id;
    private Long userId;
    private String periodType;
    private Integer score;
    private Integer rank;
    private LocalDate snapshotDate;

}
