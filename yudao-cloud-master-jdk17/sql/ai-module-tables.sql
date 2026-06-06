-- ========================================
-- yudao-module-ai 数据库表结构
-- ========================================

-- 1. AI API 秘钥
CREATE TABLE `ai_api_key` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '名称',
  `api_key` varchar(500) NOT NULL COMMENT '密钥',
  `platform` varchar(50) NOT NULL COMMENT '平台',
  `url` varchar(500) DEFAULT NULL COMMENT 'API 地址',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-开�?1-关闭)',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI API 秘钥';

-- 2. AI 模型
CREATE TABLE `ai_model` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `key_id` bigint NOT NULL COMMENT 'API 秘钥编号',
  `name` varchar(100) NOT NULL COMMENT '模型名称',
  `model` varchar(100) NOT NULL COMMENT '模型标志',
  `platform` varchar(50) NOT NULL COMMENT '平台',
  `type` tinyint NOT NULL COMMENT '类型',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序�?,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-开�?1-关闭)',
  `temperature` double DEFAULT NULL COMMENT '温度参数',
  `max_tokens` int DEFAULT NULL COMMENT '最�?Token 数量',
  `max_contexts` int DEFAULT NULL COMMENT '上下文最�?Message 数量',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 模型';

-- 3. AI 聊天角色
CREATE TABLE `ai_chat_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '角色名称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '角色头像',
  `category` varchar(50) DEFAULT NULL COMMENT '角色分类',
  `description` varchar(500) DEFAULT NULL COMMENT '角色描述',
  `system_message` text COMMENT '角色设定',
  `user_id` bigint DEFAULT NULL COMMENT '用户编号',
  `model_id` bigint DEFAULT NULL COMMENT '模型编号',
  `knowledge_ids` json DEFAULT NULL COMMENT '引用的知识库编号列表',
  `tool_ids` json DEFAULT NULL COMMENT '引用的工具编号列�?,
  `mcp_client_names` json DEFAULT NULL COMMENT '引用�?MCP Client 名字列表',
  `public_status` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否公开',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序�?,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-开�?1-关闭)',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 聊天角色';

-- 4. AI 工具
CREATE TABLE `ai_tool` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '工具编号',
  `name` varchar(100) NOT NULL COMMENT '工具名称',
  `description` varchar(500) DEFAULT NULL COMMENT '工具描述',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-开�?1-关闭)',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 工具';

-- 5. AI 聊天对话
CREATE TABLE `ai_chat_conversation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID 编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `title` varchar(255) DEFAULT '新对�? COMMENT '对话标题',
  `pinned` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否置顶',
  `pinned_time` datetime DEFAULT NULL COMMENT '置顶时间',
  `role_id` bigint DEFAULT NULL COMMENT '角色编号',
  `model_id` bigint NOT NULL COMMENT '模型编号',
  `model` varchar(100) NOT NULL COMMENT '模型标志',
  `system_message` text COMMENT '角色设定',
  `temperature` double DEFAULT NULL COMMENT '温度参数',
  `max_tokens` int DEFAULT NULL COMMENT '最�?Token 数量',
  `max_contexts` int DEFAULT NULL COMMENT '上下文最�?Message 数量',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 聊天对话';

-- 6. AI 聊天消息
CREATE TABLE `ai_chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `conversation_id` bigint NOT NULL COMMENT '对话编号',
  `reply_id` bigint DEFAULT NULL COMMENT '回复消息编号',
  `type` varchar(50) NOT NULL COMMENT '消息类型(USER/ASSISTANT)',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `role_id` bigint DEFAULT NULL COMMENT '角色编号',
  `model` varchar(100) DEFAULT NULL COMMENT '模型标志',
  `model_id` bigint DEFAULT NULL COMMENT '模型编号',
  `content` longtext COMMENT '聊天内容',
  `reasoning_content` longtext COMMENT '推理内容',
  `use_context` bit(1) DEFAULT NULL COMMENT '是否携带上下�?,
  `segment_ids` json DEFAULT NULL COMMENT '知识库段落编号数�?,
  `web_search_pages` json DEFAULT NULL COMMENT '联网搜索网页内容数组',
  `attachment_urls` json DEFAULT NULL COMMENT '附件 URL 数组',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 聊天消息';

-- 7. AI 知识�?CREATE TABLE `ai_knowledge` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '知识库名�?,
  `description` varchar(500) DEFAULT NULL COMMENT '知识库描�?,
  `embedding_model_id` bigint DEFAULT NULL COMMENT '向量模型编号',
  `embedding_model` varchar(100) DEFAULT NULL COMMENT '模型标识',
  `top_k` int DEFAULT NULL COMMENT 'topK',
  `similarity_threshold` double DEFAULT NULL COMMENT '相似度阈�?,
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-开�?1-关闭)',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 知识�?;

-- 8. AI 知识�?文档
CREATE TABLE `ai_knowledge_document` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `knowledge_id` bigint NOT NULL COMMENT '知识库编�?,
  `name` varchar(255) NOT NULL COMMENT '文档名称',
  `url` varchar(500) DEFAULT NULL COMMENT '文件 URL',
  `content` longtext COMMENT '内容',
  `content_length` int DEFAULT NULL COMMENT '文档长度',
  `tokens` int DEFAULT NULL COMMENT '文档 token 数量',
  `segment_max_tokens` int DEFAULT NULL COMMENT '分片最�?Token �?,
  `retrieval_count` int NOT NULL DEFAULT '0' COMMENT '召回次数',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-启用 1-禁用)',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 知识�?文档';

-- 9. AI 知识�?文档分段
CREATE TABLE `ai_knowledge_segment` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `knowledge_id` bigint NOT NULL COMMENT '知识库编�?,
  `document_id` bigint NOT NULL COMMENT '文档编号',
  `content` longtext NOT NULL COMMENT '切片内容',
  `content_length` int DEFAULT NULL COMMENT '切片内容长度',
  `vector_id` varchar(255) DEFAULT '' COMMENT '向量库的编号',
  `tokens` int DEFAULT NULL COMMENT 'token 数量',
  `retrieval_count` int NOT NULL DEFAULT '0' COMMENT '召回次数',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-启用 1-禁用)',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 知识�?文档分段';

-- 10. AI 绘画
CREATE TABLE `ai_image` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `prompt` text NOT NULL COMMENT '提示�?,
  `platform` varchar(50) NOT NULL COMMENT '平台',
  `model_id` bigint DEFAULT NULL COMMENT '模型编号',
  `model` varchar(100) DEFAULT NULL COMMENT '模型标识',
  `width` int DEFAULT NULL COMMENT '图片宽度',
  `height` int DEFAULT NULL COMMENT '图片高度',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '生成状�?,
  `finish_time` datetime DEFAULT NULL COMMENT '完成时间',
  `error_message` varchar(500) DEFAULT NULL COMMENT '绘画错误信息',
  `pic_url` varchar(500) DEFAULT NULL COMMENT '图片地址',
  `public_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否公开',
  `options` json DEFAULT NULL COMMENT '绘制参数',
  `buttons` json DEFAULT NULL COMMENT 'mj buttons 按钮',
  `task_id` varchar(255) DEFAULT NULL COMMENT '任务编号',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 绘画';

-- 11. AI 音乐
CREATE TABLE `ai_music` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `title` varchar(255) DEFAULT NULL COMMENT '音乐名称',
  `lyric` longtext COMMENT '歌词',
  `image_url` varchar(500) DEFAULT NULL COMMENT '图片地址',
  `audio_url` varchar(500) DEFAULT NULL COMMENT '音频地址',
  `video_url` varchar(500) DEFAULT NULL COMMENT '视频地址',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '音乐状�?,
  `generate_mode` tinyint DEFAULT NULL COMMENT '生成模式',
  `description` text COMMENT '描述�?,
  `platform` varchar(50) DEFAULT NULL COMMENT '平台',
  `model` varchar(100) DEFAULT NULL COMMENT '模型',
  `tags` json DEFAULT NULL COMMENT '音乐风格标签',
  `duration` double DEFAULT NULL COMMENT '音乐时长',
  `public_status` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否公开',
  `task_id` varchar(255) DEFAULT NULL COMMENT '任务编号',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 音乐';

-- 12. AI 思维导图
CREATE TABLE `ai_mind_map` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `platform` varchar(50) NOT NULL COMMENT '平台',
  `model_id` bigint DEFAULT NULL COMMENT '模型编号',
  `model` varchar(100) DEFAULT NULL COMMENT '模型',
  `prompt` text NOT NULL COMMENT '生成内容提示',
  `generated_content` longtext COMMENT '生成的内�?,
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 思维导图';

-- 13. AI 写作
CREATE TABLE `ai_write` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `type` tinyint NOT NULL COMMENT '写作类型',
  `platform` varchar(50) NOT NULL COMMENT '平台',
  `model_id` bigint DEFAULT NULL COMMENT '模型编号',
  `model` varchar(100) DEFAULT NULL COMMENT '模型',
  `prompt` text NOT NULL COMMENT '生成内容提示',
  `generated_content` longtext COMMENT '生成的内�?,
  `original_content` longtext COMMENT '原文',
  `length` int DEFAULT NULL COMMENT '长度提示�?,
  `format` int DEFAULT NULL COMMENT '格式提示�?,
  `tone` int DEFAULT NULL COMMENT '语气提示�?,
  `language` int DEFAULT NULL COMMENT '语言提示�?,
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 写作';

-- 14. AI 工作�?CREATE TABLE `ai_workflow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '工作流名�?,
  `code` varchar(100) NOT NULL COMMENT '工作流标�?,
  `graph` longtext COMMENT '工作流模�?JSON 数据',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状�?0-开�?1-关闭)',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 工作�?;

-- ========================================
-- AI 教学模块（education�?-- ========================================

-- 15. AI 智能辅导会话
CREATE TABLE `ai_tutoring_session` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `title` varchar(255) DEFAULT NULL COMMENT '会话标题',
  `question` text COMMENT '初始问题',
  `context_json` longtext COMMENT '上下�?JSON',
  `status` varchar(20) DEFAULT NULL COMMENT '状�?,
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 智能辅导会话';

-- 16. AI 智能辅导消息
CREATE TABLE `ai_tutoring_message` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `session_id` bigint NOT NULL COMMENT '会话编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `role` varchar(20) NOT NULL COMMENT '角色(user/assistant/system)',
  `content_type` varchar(50) DEFAULT NULL COMMENT '内容类型',
  `content` longtext COMMENT '内容',
  `content_json` longtext COMMENT '内容 JSON',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 智能辅导消息';

-- 17. AI 学生画像
CREATE TABLE `ai_student_profile` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `grade` varchar(50) DEFAULT NULL COMMENT '年级',
  `learning_goals` text COMMENT '学习目标',
  `knowledge_level` varchar(50) DEFAULT NULL COMMENT '知识水平',
  `learning_preferences` varchar(500) DEFAULT NULL COMMENT '学习偏好',
  `weak_points` text COMMENT '薄弱�?,
  `strong_points` text COMMENT '优势�?,
  `study_history` longtext COMMENT '学习历史',
  `learning_speed` int DEFAULT NULL COMMENT '学习速度',
  `preferred_resource_types` varchar(500) DEFAULT NULL COMMENT '偏好资源类型',
  `study_time_preference` varchar(100) DEFAULT NULL COMMENT '学习时间偏好',
  `status` varchar(20) DEFAULT NULL COMMENT '状�?,
  `profile_json` longtext COMMENT '画像 JSON',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学生画像';

-- 18. AI 学习路径
CREATE TABLE `ai_learning_path` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `profile_id` bigint DEFAULT NULL COMMENT '画像编号',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `goal` text COMMENT '目标',
  `description` text COMMENT '描述',
  `total_nodes` int NOT NULL DEFAULT '0' COMMENT '总节点数',
  `completed_nodes` int NOT NULL DEFAULT '0' COMMENT '已完成节点数',
  `status` varchar(20) DEFAULT NULL COMMENT '状�?,
  `start_time` datetime DEFAULT NULL COMMENT '开始时�?,
  `expected_end_time` datetime DEFAULT NULL COMMENT '预计结束时间',
  `completed_time` datetime DEFAULT NULL COMMENT '完成时间',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习路径';

-- 19. AI 学习路径节点
CREATE TABLE `ai_learning_path_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `path_id` bigint NOT NULL COMMENT '路径编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `description` text COMMENT '描述',
  `content` longtext COMMENT '内容',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` varchar(20) DEFAULT NULL COMMENT '状�?,
  `start_time` datetime DEFAULT NULL COMMENT '开始时�?,
  `completed_time` datetime DEFAULT NULL COMMENT '完成时间',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习路径节点';

-- 20. AI 学习资源
CREATE TABLE `ai_learning_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `profile_id` bigint DEFAULT NULL COMMENT '画像编号',
  `title` varchar(255) DEFAULT NULL COMMENT '标题',
  `resource_type` varchar(50) DEFAULT NULL COMMENT '资源类型',
  `content` longtext COMMENT '内容',
  `content_json` longtext COMMENT '内容 JSON',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签',
  `difficulty` varchar(50) DEFAULT NULL COMMENT '难度',
  `related_course_id` bigint DEFAULT NULL COMMENT '关联课程编号',
  `course_name` varchar(255) DEFAULT NULL COMMENT '课程名称',
  `status` varchar(20) DEFAULT NULL COMMENT '状�?,
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `progress` int DEFAULT NULL COMMENT '进度',
  `feedback` text COMMENT '反馈',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习资源';

-- 21. AI 学习评估
CREATE TABLE `ai_learning_evaluation` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `profile_id` bigint DEFAULT NULL COMMENT '画像编号',
  `evaluation_type` varchar(50) DEFAULT NULL COMMENT '评估类型',
  `dimension` varchar(50) DEFAULT NULL COMMENT '评估维度',
  `score` int DEFAULT NULL COMMENT '得分',
  `max_score` int DEFAULT NULL COMMENT '满分',
  `evaluation` text COMMENT '评估内容',
  `suggestion` text COMMENT '建议',
  `evaluation_json` longtext COMMENT '评估 JSON',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建�?,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新�?,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习评估';

-- ========================================
-- AI 配置模块（config）
-- ========================================

-- 22. AI 提示词模板
CREATE TABLE `ai_prompt_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '模板名称',
  `category` varchar(50) DEFAULT NULL COMMENT '模板分类',
  `description` varchar(500) DEFAULT NULL COMMENT '模板描述',
  `content` text NOT NULL COMMENT '提示词内容',
  `variables` json DEFAULT NULL COMMENT '变量列表（JSON数组）',
  `type` tinyint NOT NULL DEFAULT '0' COMMENT '类型（0-系统模板 1-用户自定义模板）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-开启 1-关闭）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 提示词模板';

-- 23. AI 敏感词
CREATE TABLE `ai_sensitive_word` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `word` varchar(100) NOT NULL COMMENT '敏感词',
  `level` tinyint NOT NULL DEFAULT '0' COMMENT '敏感等级（0-一般 1-中等 2-严重）',
  `category` varchar(50) DEFAULT NULL COMMENT '分类（政治/色情/暴力/违法/广告/其他）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-开启 1-关闭）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_word` (`word`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 敏感词';

-- 24. AI API 调用统计
CREATE TABLE `ai_api_statistics` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `model_id` bigint DEFAULT NULL COMMENT '模型编号',
  `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `platform` varchar(50) DEFAULT NULL COMMENT '平台',
  `api_type` varchar(50) NOT NULL COMMENT '接口类型（chat/image/music/knowledge等）',
  `call_count` int NOT NULL DEFAULT '1' COMMENT '调用次数',
  `input_tokens` int NOT NULL DEFAULT '0' COMMENT '输入Token数',
  `output_tokens` int NOT NULL DEFAULT '0' COMMENT '输出Token数',
  `total_tokens` int NOT NULL DEFAULT '0' COMMENT '总Token数',
  `duration_ms` bigint DEFAULT NULL COMMENT '响应时长（毫秒）',
  `success` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否成功',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_model_id` (`model_id`),
  KEY `idx_api_type` (`api_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI API 调用统计';

-- 25. AI 用户调用配额
CREATE TABLE `ai_user_quota` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `quota_type` tinyint NOT NULL DEFAULT '0' COMMENT '配额类型（0-按次 1-按Token 2-按时长）',
  `daily_limit` bigint NOT NULL DEFAULT '0' COMMENT '每日限额（0-不限制）',
  `monthly_limit` bigint NOT NULL DEFAULT '0' COMMENT '每月限额（0-不限制）',
  `total_limit` bigint NOT NULL DEFAULT '0' COMMENT '总额限制（0-不限制）',
  `daily_used` bigint NOT NULL DEFAULT '0' COMMENT '今日已用',
  `monthly_used` bigint NOT NULL DEFAULT '0' COMMENT '本月已用',
  `total_used` bigint NOT NULL DEFAULT '0' COMMENT '累计已用',
  `last_reset_date` date DEFAULT NULL COMMENT '上次重置日期',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-启用 1-禁用）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 用户调用配额';

-- 26. AI 对话日志（管理审计用）
CREATE TABLE `ai_conversation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `conversation_id` bigint NOT NULL COMMENT '对话编号',
  `message_id` bigint DEFAULT NULL COMMENT '消息编号',
  `model_name` varchar(100) DEFAULT NULL COMMENT '模型名称',
  `prompt` longtext COMMENT '用户输入',
  `completion` longtext COMMENT 'AI回复',
  `input_tokens` int DEFAULT NULL COMMENT '输入Token数',
  `output_tokens` int DEFAULT NULL COMMENT '输出Token数',
  `duration_ms` bigint DEFAULT NULL COMMENT '响应时长（毫秒）',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT 'UserAgent',
  `success` bit(1) NOT NULL DEFAULT b'1' COMMENT '是否成功',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 对话日志';

-- 27. AI 系统配置
CREATE TABLE `ai_system_config` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` varchar(500) NOT NULL COMMENT '配置值',
  `value_type` varchar(20) NOT NULL DEFAULT 'STRING' COMMENT '值类型（STRING/INTEGER/BOOLEAN/DOUBLE/JSON）',
  `description` varchar(500) DEFAULT NULL COMMENT '配置描述',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-启用 1-禁用）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 系统配置';

-- ========================================
-- AI 学习社交模块（social）
-- ========================================

-- 28. AI 签到记录
CREATE TABLE `ai_check_in_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `check_in_date` date NOT NULL COMMENT '签到日期',
  `streak_days` int NOT NULL DEFAULT '1' COMMENT '连续签到天数',
  `total_days` int NOT NULL DEFAULT '1' COMMENT '累计签到天数',
  `points_earned` int NOT NULL DEFAULT '0' COMMENT '本次获得积分',
  `ai_encouragement` text COMMENT 'AI 生成的鼓励语',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-正常 1-补签）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_date` (`user_id`, `check_in_date`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 签到记录';

-- 29. AI 用户积分
CREATE TABLE `ai_user_points` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `total_points` int NOT NULL DEFAULT '0' COMMENT '总积分',
  `weekly_points` int NOT NULL DEFAULT '0' COMMENT '本周积分',
  `monthly_points` int NOT NULL DEFAULT '0' COMMENT '本月积分',
  `level` int NOT NULL DEFAULT '1' COMMENT '等级',
  `rank_title` varchar(50) DEFAULT '学习新手' COMMENT '等级称号',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 用户积分';

-- 30. AI 好友关系
CREATE TABLE `ai_friend` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `friend_user_id` bigint NOT NULL COMMENT '好友用户编号',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-待确认 1-已接受 2-已拉黑）',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_friend` (`user_id`, `friend_user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_friend_user_id` (`friend_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 好友关系';

-- 31. AI 关注关系
CREATE TABLE `ai_follow` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号（关注者）',
  `follow_user_id` bigint NOT NULL COMMENT '被关注用户编号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-取消 1-关注中）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_follow` (`user_id`, `follow_user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_follow_user_id` (`follow_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 关注关系';

-- 32. AI 排行榜记录
CREATE TABLE `ai_leaderboard_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `period_type` varchar(20) NOT NULL COMMENT '周期类型（WEEKLY/MONTHLY）',
  `score` int NOT NULL DEFAULT '0' COMMENT '得分',
  `rank` int DEFAULT NULL COMMENT '排名',
  `snapshot_date` date NOT NULL COMMENT '快照日期',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_period` (`user_id`, `period_type`, `snapshot_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 排行榜记录';

-- 33. AI 学习目标
CREATE TABLE `ai_learning_goal` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `goal_type` varchar(50) NOT NULL COMMENT '目标类型（STUDY_MINUTES/RESOURCE_COUNT/CHECK_IN_STREAK/POINTS_TARGET）',
  `title` varchar(255) NOT NULL COMMENT '目标标题',
  `description` varchar(500) DEFAULT NULL COMMENT '目标描述',
  `target_value` int NOT NULL COMMENT '目标值',
  `current_value` int NOT NULL DEFAULT '0' COMMENT '当前值',
  `deadline` date DEFAULT NULL COMMENT '截止日期',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态（ACTIVE/COMPLETED/FAILED）',
  `ai_feedback` text COMMENT 'AI 完成点评',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习目标';

-- 34. AI 用户动态
CREATE TABLE `ai_user_activity` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `activity_type` varchar(50) NOT NULL COMMENT '动态类型（CHECK_IN/COMPLETE_RESOURCE/EARN_POINTS/LEVEL_UP/ADD_FRIEND/COMPLETE_GOAL）',
  `content` varchar(500) DEFAULT NULL COMMENT '动态内容',
  `ref_id` bigint DEFAULT NULL COMMENT '关联对象编号',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 用户动态';

-- ========================================
-- AI 学校与课程模块（school）
-- ========================================

-- 35. AI 学校信息
CREATE TABLE `ai_school` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(200) NOT NULL COMMENT '学校名称',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `type` varchar(50) DEFAULT NULL COMMENT '学校类型（UNIVERSITY/COLLEGE/HIGH_SCHOOL/MIDDLE_SCHOOL/PRIMARY/OTHER）',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-开启 1-关闭）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学校信息';

-- 36. AI 学生学校关联
CREATE TABLE `ai_student_school` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `school_id` bigint NOT NULL COMMENT '学校编号',
  `major` varchar(100) DEFAULT NULL COMMENT '专业',
  `grade` varchar(20) DEFAULT NULL COMMENT '年级（如：2024级）',
  `class_name` varchar(100) DEFAULT NULL COMMENT '班级',
  `student_no` varchar(50) DEFAULT NULL COMMENT '学号',
  `enrollment_year` int DEFAULT NULL COMMENT '入学年份',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-在读 1-毕业 2-休学）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_school_id` (`school_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学生学校关联';

-- 37. AI 课程表
CREATE TABLE `ai_course_schedule` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint DEFAULT NULL COMMENT '用户编号（管理员导入时可为 NULL）',
  `school_id` bigint DEFAULT NULL COMMENT '学校编号（管理员批量导入时关联）',
  `course_name` varchar(200) NOT NULL COMMENT '课程名称',
  `teacher` varchar(100) DEFAULT NULL COMMENT '授课教师',
  `classroom` varchar(100) DEFAULT NULL COMMENT '上课地点',
  `day_of_week` tinyint NOT NULL COMMENT '星期（1-7，1=周一）',
  `start_time` varchar(10) NOT NULL COMMENT '开始时间（HH:mm）',
  `end_time` varchar(10) NOT NULL COMMENT '结束时间（HH:mm）',
  `start_period` int DEFAULT NULL COMMENT '开始节次',
  `end_period` int DEFAULT NULL COMMENT '结束节次',
  `color` varchar(20) DEFAULT '#409eff' COMMENT '课程颜色',
  `course_type` varchar(50) DEFAULT NULL COMMENT '课程类型（REQUIRED/ELECTIVE/PUBLIC）',
  `semester` varchar(50) DEFAULT NULL COMMENT '学期（如：2024-2025-1）',
  `week_type` varchar(20) DEFAULT 'EVERY' COMMENT '周类型（EVERY/ODD/EVEN-全部/单周/双周）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-正常 1-结课）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_school_id` (`school_id`),
  KEY `idx_day_of_week` (`day_of_week`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 课程表';

-- 38. AI 学科分类
CREATE TABLE `ai_subject_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(50) NOT NULL COMMENT '学科名称',
  `code` varchar(50) NOT NULL COMMENT '学科编码',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父学科编号',
  `icon` varchar(200) DEFAULT NULL COMMENT '图标',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-开启 1-关闭）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学科分类';

-- 39. AI 知识点标签
CREATE TABLE `ai_knowledge_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `subject_id` bigint NOT NULL COMMENT '所属学科编号',
  `name` varchar(100) NOT NULL COMMENT '标签名称',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `difficulty` tinyint DEFAULT '1' COMMENT '难度（1-5）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-开启 1-关闭）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subject_id` (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 知识点标签';

-- 40. AI 题库
CREATE TABLE `ai_question_bank` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `subject_id` bigint DEFAULT NULL COMMENT '学科编号',
  `knowledge_tag_ids` json DEFAULT NULL COMMENT '关联知识点标签编号',
  `question_type` varchar(30) NOT NULL COMMENT '题目类型（CHOICE/JUDGE/SHORT_ANSWER/PROGRAMMING）',
  `difficulty` tinyint NOT NULL DEFAULT '1' COMMENT '难度（1-5）',
  `title` text NOT NULL COMMENT '题目标题/题干',
  `content` longtext COMMENT '题目内容（富文本）',
  `options` json DEFAULT NULL COMMENT '选择题选项 JSON',
  `answer` text COMMENT '正确答案',
  `analysis` longtext COMMENT '答案解析',
  `programming_config` json DEFAULT NULL COMMENT '编程题配置',
  `source` varchar(50) NOT NULL DEFAULT 'MANUAL' COMMENT '来源（MANUAL/AI_GENERATED/IMPORTED）',
  `ai_generated_prompt` text COMMENT 'AI 生成时的提示词',
  `status` tinyint NOT NULL DEFAULT '0' COMMENT '状态（0-启用 1-关闭）',
  `sort` int NOT NULL DEFAULT '0' COMMENT '排序',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_question_type` (`question_type`),
  KEY `idx_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 题库';

-- 41. AI 错题本
CREATE TABLE `ai_wrong_answer_book` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `question_id` bigint NOT NULL COMMENT '题目编号',
  `subject_id` bigint DEFAULT NULL COMMENT '学科编号',
  `knowledge_tag_ids` json DEFAULT NULL COMMENT '关联知识点标签',
  `user_answer` text COMMENT '用户答案',
  `correct_answer` text COMMENT '正确答案',
  `is_correct` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否正确',
  `error_type` varchar(50) DEFAULT NULL COMMENT '错误类型',
  `error_analysis` text COMMENT 'AI 错误分析',
  `review_count` int NOT NULL DEFAULT '0' COMMENT '复习次数',
  `last_review_time` datetime DEFAULT NULL COMMENT '上次复习时间',
  `next_review_time` datetime DEFAULT NULL COMMENT '下次复习时间',
  `mastery_level` tinyint NOT NULL DEFAULT '0' COMMENT '掌握程度（0-5）',
  `source_type` varchar(50) DEFAULT NULL COMMENT '来源类型（HOMEWORK/EXAM/PRACTICE）',
  `source_id` bigint DEFAULT NULL COMMENT '来源编号',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_mastery_level` (`mastery_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 错题本';

-- 42. AI 作业
CREATE TABLE `ai_homework` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `title` varchar(200) NOT NULL COMMENT '作业标题',
  `description` text COMMENT '作业描述',
  `subject_id` bigint DEFAULT NULL COMMENT '学科编号',
  `question_ids` json NOT NULL COMMENT '题目编号列表',
  `total_score` int NOT NULL DEFAULT '100' COMMENT '总分',
  `pass_score` int NOT NULL DEFAULT '60' COMMENT '及格分',
  `deadline` datetime DEFAULT NULL COMMENT '截止时间',
  `time_limit` int DEFAULT NULL COMMENT '限时（分钟）',
  `allow_redo` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否允许重做',
  `max_redo_count` int DEFAULT '0' COMMENT '最大重做次数',
  `publish_status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '发布状态',
  `publish_time` datetime DEFAULT NULL COMMENT '发布时间',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_publish_status` (`publish_status`),
  KEY `idx_deadline` (`deadline`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 作业';

-- 43. AI 作业提交
CREATE TABLE `ai_homework_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `homework_id` bigint NOT NULL COMMENT '作业编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `answers` json NOT NULL COMMENT '答案JSON',
  `submit_time` datetime DEFAULT NULL COMMENT '提交时间',
  `total_score` int DEFAULT NULL COMMENT '总分',
  `ai_score` int DEFAULT NULL COMMENT 'AI评分',
  `ai_feedback` longtext COMMENT 'AI评语',
  `grade_status` varchar(20) NOT NULL DEFAULT 'UNGRADED' COMMENT '批改状态',
  `grade_detail` json DEFAULT NULL COMMENT '批改详情JSON',
  `redo_count` int NOT NULL DEFAULT '0' COMMENT '重做次数',
  `duration_seconds` int DEFAULT NULL COMMENT '作答时长（秒）',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_homework_user` (`homework_id`, `user_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_grade_status` (`grade_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 作业提交';

-- 44. AI 模拟考试
CREATE TABLE `ai_exam` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `title` varchar(200) NOT NULL COMMENT '考试标题',
  `subject_id` bigint DEFAULT NULL COMMENT '学科编号',
  `description` text COMMENT '考试说明',
  `question_ids` json NOT NULL COMMENT '题目编号列表',
  `total_score` int NOT NULL DEFAULT '100' COMMENT '总分',
  `pass_score` int NOT NULL DEFAULT '60' COMMENT '及格分',
  `time_limit` int NOT NULL DEFAULT '120' COMMENT '限时（分钟）',
  `difficulty` tinyint DEFAULT '1' COMMENT '难度（1-5）',
  `generate_type` varchar(20) NOT NULL DEFAULT 'MANUAL' COMMENT '生成方式',
  `publish_status` varchar(20) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `allow_retake` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否允许重考',
  `max_retake_count` int DEFAULT '0' COMMENT '最大重考次数',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_subject_id` (`subject_id`),
  KEY `idx_publish_status` (`publish_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 模拟考试';

-- 45. AI 考试记录
CREATE TABLE `ai_exam_record` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `exam_id` bigint NOT NULL COMMENT '考试编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `submit_time` datetime DEFAULT NULL COMMENT '交卷时间',
  `answers` json DEFAULT NULL COMMENT '答案JSON',
  `total_score` int DEFAULT NULL COMMENT '总分',
  `ai_score` int DEFAULT NULL COMMENT 'AI评分',
  `ai_feedback` longtext COMMENT 'AI评语',
  `grade_detail` json DEFAULT NULL COMMENT '每题得分详情',
  `duration_seconds` int DEFAULT NULL COMMENT '用时（秒）',
  `status` varchar(20) NOT NULL DEFAULT 'IN_PROGRESS' COMMENT '状态',
  `retake_count` int NOT NULL DEFAULT '0' COMMENT '第几次重考',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_exam_id` (`exam_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 考试记录';

-- 46. AI 消息通知
CREATE TABLE `ai_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `notification_type` varchar(50) NOT NULL COMMENT '通知类型',
  `title` varchar(200) NOT NULL COMMENT '通知标题',
  `content` text COMMENT '通知内容',
  `ref_id` bigint DEFAULT NULL COMMENT '关联对象编号',
  `ref_type` varchar(50) DEFAULT NULL COMMENT '关联对象类型',
  `is_read` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否已读',
  `read_time` datetime DEFAULT NULL COMMENT '读时间',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 消息通知';

-- 47. AI 学习计划
CREATE TABLE `ai_study_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `user_id` bigint NOT NULL COMMENT '用户编号',
  `title` varchar(200) NOT NULL COMMENT '计划标题',
  `plan_type` varchar(20) NOT NULL COMMENT '计划类型（WEEKLY/MONTHLY）',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `goal` text COMMENT '计划目标',
  `description` text COMMENT '计划描述',
  `ai_generated_content` longtext COMMENT 'AI生成内容JSON',
  `daily_plans` json DEFAULT NULL COMMENT '每日计划JSON',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
  `progress` int NOT NULL DEFAULT '0' COMMENT '完成进度百分比',
  `source` varchar(50) DEFAULT 'AI' COMMENT '来源（AI/MANUAL）',
  `completed_date` datetime DEFAULT NULL COMMENT '完成时间',
  `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号',
  `creator` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updater` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习计划';
