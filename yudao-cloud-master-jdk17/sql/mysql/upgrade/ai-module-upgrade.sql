-- ============================================================================
-- AI 模块企业级升级 SQL 变更脚本
-- 执行顺序：按模块编号顺序执行
-- ============================================================================

-- M2: 可观测性 - AI API 调用详情表
-- 用于记录每次 LLM 调用的详细信息：Token 用量、延迟、费用估算、链路追踪
-- ============================================================================

CREATE TABLE IF NOT EXISTS `ai_api_call_detail` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    `user_id` BIGINT DEFAULT NULL COMMENT '用户编号',
    `model_id` BIGINT DEFAULT NULL COMMENT '模型编号',
    `model_name` VARCHAR(64) DEFAULT NULL COMMENT '模型名称',
    `platform` VARCHAR(32) DEFAULT NULL COMMENT '平台',
    `api_type` VARCHAR(32) DEFAULT NULL COMMENT 'API 类型(chat/embedding/image/music)',
    `prompt_snippet` VARCHAR(500) DEFAULT NULL COMMENT 'Prompt 摘要',
    `completion_snippet` VARCHAR(500) DEFAULT NULL COMMENT '响应摘要',
    `prompt_tokens` INT DEFAULT 0 COMMENT 'Prompt Token 数',
    `completion_tokens` INT DEFAULT 0 COMMENT 'Completion Token 数',
    `total_tokens` INT DEFAULT 0 COMMENT '总 Token 数',
    `latency_ms` INT DEFAULT 0 COMMENT '延迟(毫秒)',
    `estimated_cost` DECIMAL(10,6) DEFAULT 0.0 COMMENT '估算费用(美元)',
    `success` TINYINT DEFAULT 1 COMMENT '是否成功',
    `error_message` VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `trace_id` VARCHAR(64) DEFAULT NULL COMMENT '链路追踪ID',
    `conversation_id` BIGINT DEFAULT NULL COMMENT '对话ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_model_id` (`model_id`),
    INDEX `idx_trace_id` (`trace_id`),
    INDEX `idx_create_time` (`create_time`),
    INDEX `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI API 调用详情表';


-- M4: Prompt 管理 - 模板版本化字段
-- ============================================================================

ALTER TABLE `ai_prompt_template`
    ADD COLUMN IF NOT EXISTS `version` INT DEFAULT 1 COMMENT '版本号',
    ADD COLUMN IF NOT EXISTS `variables` VARCHAR(1000) DEFAULT NULL COMMENT '模板变量JSON(如["{{topic}}","{{style}}"])',
    ADD COLUMN IF NOT EXISTS `ab_group` VARCHAR(16) DEFAULT NULL COMMENT 'A/B测试分组(A/B/NULL)',
    ADD COLUMN IF NOT EXISTS `parent_id` BIGINT DEFAULT NULL COMMENT '上一版本ID';


-- M8: 对话记忆 - 对话摘要字段
-- ============================================================================

ALTER TABLE `ai_chat_conversation`
    ADD COLUMN IF NOT EXISTS `summary` TEXT DEFAULT NULL COMMENT '对话历史摘要',
    ADD COLUMN IF NOT EXISTS `summary_updated_at` DATETIME DEFAULT NULL COMMENT '摘要更新时间';


-- M3: 语义缓存 - 缓存条目表（可选，主要用于持久化缓存）
-- ============================================================================

CREATE TABLE IF NOT EXISTS `ai_cache_entry` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '编号',
    `cache_key` VARCHAR(128) NOT NULL COMMENT '缓存Key(MD5)',
    `embedding` JSON DEFAULT NULL COMMENT 'Query Embedding向量',
    `question` TEXT COMMENT '原始问题',
    `response` TEXT COMMENT '缓存响应',
    `model_name` VARCHAR(64) DEFAULT NULL COMMENT '模型名称',
    `hit_count` INT DEFAULT 0 COMMENT '命中次数',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_cache_key` (`cache_key`),
    INDEX `idx_expire_time` (`expire_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 语义缓存表';
