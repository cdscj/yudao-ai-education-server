package cn.iocoder.yudao.module.ai.dal.dataobject.chat;

import cn.iocoder.yudao.framework.tenant.core.aop.TenantIgnore;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * AI 对话日志 DO
 *
 * 仅追加写入，记录每次对话的完整调用日志
 *
 * @author 芋道源码
 */
@TableName("ai_conversation_log")
@KeySequence("ai_conversation_log_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TenantIgnore
public class AiConversationLogDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 用户编号
     */
    private Long userId;
    /**
     * 对话编号
     */
    private Long conversationId;
    /**
     * 消息编号
     */
    private Long messageId;
    /**
     * 模型名称
     */
    private String modelName;
    /**
     * 提示词
     */
    private String prompt;
    /**
     * 回复内容
     */
    private String completion;
    /**
     * 输入 Token 数
     */
    private Integer inputTokens;
    /**
     * 输出 Token 数
     */
    private Integer outputTokens;
    /**
     * 耗时（毫秒）
     */
    private Long durationMs;
    /**
     * IP 地址
     */
    private String ipAddress;
    /**
     * User-Agent
     */
    private String userAgent;
    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 错误信息
     */
    private String errorMessage;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

}
