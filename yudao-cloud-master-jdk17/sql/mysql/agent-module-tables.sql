-- ========================================
-- yudao-module-ai Agent 模块 DDL
-- ========================================

-- 1. AI Agent 配置表
CREATE TABLE `ai_agent_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT 'Agent 名称',
  `type` tinyint NOT NULL COMMENT 'Agent 类型（1-CODE 2-RESEARCH 3-WRITE 4-TUTOR 5-GENERAL 6-PROFILE 7-RESOURCE 8-PATH 9-MEMORY）',
  `model_id` bigint NOT NULL COMMENT '关联的 AI 模型编号',
  `system_prompt` text COMMENT '系统提示词（可覆盖 Agent 默认提示词）',
  `description` varchar(500) DEFAULT NULL COMMENT 'Agent 描述',
  `max_steps` int DEFAULT 10 COMMENT '最大推理步数（ReAct 模式）',
  `temperature` double DEFAULT NULL COMMENT '温度参数',
  `max_tokens` int DEFAULT NULL COMMENT '单次回复最大 Token 数',
  `sort` int NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` tinyint NOT NULL DEFAULT 0 COMMENT '状态（0-开启 1-关闭）',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI Agent 配置';

-- 2. AI Agent 会话表
CREATE TABLE `ai_agent_conversation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `agent_id` bigint NOT NULL COMMENT 'Agent 配置编号',
  `title` varchar(200) NOT NULL COMMENT '会话标题',
  `model_id` bigint DEFAULT NULL COMMENT '使用的 AI 模型编号',
  `active` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否活跃（1-进行中 0-已结束）',
  `message_count` int NOT NULL DEFAULT 0 COMMENT '消息数量',
  `total_tokens` int NOT NULL DEFAULT 0 COMMENT '使用的 Token 总数',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_agent_id` (`agent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI Agent 会话';

-- 3. AI Agent 消息表
CREATE TABLE `ai_agent_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `conversation_id` bigint NOT NULL COMMENT '会话编号',
  `role` varchar(20) NOT NULL COMMENT '消息角色（user/assistant）',
  `content` mediumtext NOT NULL COMMENT '消息内容',
  `is_error` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否为错误消息',
  `usage_tokens` int NOT NULL DEFAULT 0 COMMENT '消耗的 Token 数',
  `reasoning` text COMMENT '推理过程（ReAct 中间步骤，JSON 格式）',
  `tenant_id` bigint NOT NULL DEFAULT 0 COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_conversation_id` (`conversation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI Agent 消息';
