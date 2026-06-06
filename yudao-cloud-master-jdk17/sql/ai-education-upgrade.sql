-- ============================================
-- AI 教育模块增量升级脚本
-- 在 ruoyi-vue-pro 数据库执行
-- ============================================

-- 1. 课程表增加 school_id（如果已执行可跳过）
ALTER TABLE ai_course_schedule ADD COLUMN school_id bigint DEFAULT NULL COMMENT '学校编号' AFTER user_id;
ALTER TABLE ai_course_schedule MODIFY COLUMN user_id bigint DEFAULT NULL COMMENT '用户编号';
ALTER TABLE ai_course_schedule ADD KEY idx_school_id (school_id);

-- 2. 辅导会话增加学科/知识点（如果已执行可跳过）
ALTER TABLE ai_tutoring_session ADD COLUMN subject_id bigint DEFAULT NULL COMMENT '学科编号' AFTER context_json;
ALTER TABLE ai_tutoring_session ADD COLUMN knowledge_tag_ids json DEFAULT NULL COMMENT '知识点标签' AFTER subject_id;

-- 3. Phase 1 新表
CREATE TABLE IF NOT EXISTS `ai_subject_category` (
  `id` bigint NOT NULL AUTO_INCREMENT, `name` varchar(50) NOT NULL, `code` varchar(50) NOT NULL,
  `parent_id` bigint NOT NULL DEFAULT '0', `icon` varchar(200) DEFAULT NULL, `description` varchar(500) DEFAULT NULL,
  `sort` int NOT NULL DEFAULT '0', `status` tinyint NOT NULL DEFAULT '0',
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 学科分类';

CREATE TABLE IF NOT EXISTS `ai_knowledge_tag` (
  `id` bigint NOT NULL AUTO_INCREMENT, `subject_id` bigint NOT NULL, `name` varchar(100) NOT NULL,
  `description` varchar(500) DEFAULT NULL, `difficulty` tinyint DEFAULT '1', `sort` int NOT NULL DEFAULT '0', `status` tinyint NOT NULL DEFAULT '0',
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), KEY `idx_subject_id` (`subject_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 知识点标签';

CREATE TABLE IF NOT EXISTS `ai_question_bank` (
  `id` bigint NOT NULL AUTO_INCREMENT, `subject_id` bigint DEFAULT NULL, `knowledge_tag_ids` json DEFAULT NULL,
  `question_type` varchar(30) NOT NULL, `difficulty` tinyint NOT NULL DEFAULT '1', `title` text NOT NULL, `content` longtext,
  `options` json DEFAULT NULL, `answer` text, `analysis` longtext, `programming_config` json DEFAULT NULL,
  `source` varchar(50) NOT NULL DEFAULT 'MANUAL', `ai_generated_prompt` text, `status` tinyint NOT NULL DEFAULT '0', `sort` int NOT NULL DEFAULT '0',
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`),
  KEY `idx_subject_id` (`subject_id`), KEY `idx_question_type` (`question_type`), KEY `idx_difficulty` (`difficulty`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 题库';

CREATE TABLE IF NOT EXISTS `ai_wrong_answer_book` (
  `id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `question_id` bigint NOT NULL,
  `subject_id` bigint DEFAULT NULL, `knowledge_tag_ids` json DEFAULT NULL, `user_answer` text, `correct_answer` text,
  `is_correct` bit(1) NOT NULL DEFAULT b'0', `error_type` varchar(50) DEFAULT NULL, `error_analysis` text,
  `review_count` int NOT NULL DEFAULT '0', `last_review_time` datetime DEFAULT NULL, `next_review_time` datetime DEFAULT NULL,
  `mastery_level` tinyint NOT NULL DEFAULT '0', `source_type` varchar(50) DEFAULT NULL, `source_id` bigint DEFAULT NULL,
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
  KEY `idx_user_id` (`user_id`), KEY `idx_subject_id` (`subject_id`), KEY `idx_mastery_level` (`mastery_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 错题本';

-- 4. Phase 2 新表
CREATE TABLE IF NOT EXISTS `ai_homework` (
  `id` bigint NOT NULL AUTO_INCREMENT, `title` varchar(200) NOT NULL, `description` text, `subject_id` bigint DEFAULT NULL,
  `question_ids` json NOT NULL, `total_score` int NOT NULL DEFAULT '100', `pass_score` int NOT NULL DEFAULT '60',
  `deadline` datetime DEFAULT NULL, `time_limit` int DEFAULT NULL, `allow_redo` bit(1) NOT NULL DEFAULT b'0',
  `max_redo_count` int DEFAULT '0', `publish_status` varchar(20) NOT NULL DEFAULT 'DRAFT', `publish_time` datetime DEFAULT NULL,
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), KEY `idx_subject_id` (`subject_id`), KEY `idx_publish_status` (`publish_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 作业';

CREATE TABLE IF NOT EXISTS `ai_homework_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT, `homework_id` bigint NOT NULL, `user_id` bigint NOT NULL,
  `answers` json NOT NULL, `submit_time` datetime DEFAULT NULL, `total_score` int DEFAULT NULL, `ai_score` int DEFAULT NULL,
  `ai_feedback` longtext, `grade_status` varchar(20) NOT NULL DEFAULT 'UNGRADED', `grade_detail` json DEFAULT NULL,
  `redo_count` int NOT NULL DEFAULT '0', `duration_seconds` int DEFAULT NULL,
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), UNIQUE KEY `uk_homework_user` (`homework_id`, `user_id`),
  KEY `idx_user_id` (`user_id`), KEY `idx_grade_status` (`grade_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 作业提交';

CREATE TABLE IF NOT EXISTS `ai_exam` (
  `id` bigint NOT NULL AUTO_INCREMENT, `title` varchar(200) NOT NULL, `subject_id` bigint DEFAULT NULL, `description` text,
  `question_ids` json NOT NULL, `total_score` int NOT NULL DEFAULT '100', `pass_score` int NOT NULL DEFAULT '60',
  `time_limit` int NOT NULL DEFAULT '120', `difficulty` tinyint DEFAULT '1', `generate_type` varchar(20) NOT NULL DEFAULT 'MANUAL',
  `publish_status` varchar(20) NOT NULL DEFAULT 'DRAFT', `start_time` datetime DEFAULT NULL, `end_time` datetime DEFAULT NULL,
  `allow_retake` bit(1) NOT NULL DEFAULT b'0', `max_retake_count` int DEFAULT '0',
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), KEY `idx_subject_id` (`subject_id`), KEY `idx_publish_status` (`publish_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 模拟考试';

CREATE TABLE IF NOT EXISTS `ai_exam_record` (
  `id` bigint NOT NULL AUTO_INCREMENT, `exam_id` bigint NOT NULL, `user_id` bigint NOT NULL,
  `start_time` datetime NOT NULL, `submit_time` datetime DEFAULT NULL, `answers` json DEFAULT NULL,
  `total_score` int DEFAULT NULL, `ai_score` int DEFAULT NULL, `ai_feedback` longtext, `grade_detail` json DEFAULT NULL,
  `duration_seconds` int DEFAULT NULL, `status` varchar(20) NOT NULL DEFAULT 'IN_PROGRESS', `retake_count` int NOT NULL DEFAULT '0',
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), KEY `idx_exam_id` (`exam_id`), KEY `idx_user_id` (`user_id`), KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 考试记录';

-- 5. Phase 3 新表
CREATE TABLE IF NOT EXISTS `ai_notification` (
  `id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `notification_type` varchar(50) NOT NULL,
  `title` varchar(200) NOT NULL, `content` text, `ref_id` bigint DEFAULT NULL, `ref_type` varchar(50) DEFAULT NULL,
  `is_read` bit(1) NOT NULL DEFAULT b'0', `read_time` datetime DEFAULT NULL,
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), KEY `idx_user_id` (`user_id`), KEY `idx_is_read` (`is_read`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 消息通知';

CREATE TABLE IF NOT EXISTS `ai_study_plan` (
  `id` bigint NOT NULL AUTO_INCREMENT, `user_id` bigint NOT NULL, `title` varchar(200) NOT NULL,
  `plan_type` varchar(20) NOT NULL, `start_date` date NOT NULL, `end_date` date NOT NULL,
  `goal` text, `description` text, `ai_generated_content` longtext, `daily_plans` json DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE', `progress` int NOT NULL DEFAULT '0', `source` varchar(50) DEFAULT 'AI',
  `completed_date` datetime DEFAULT NULL,
  `tenant_id` bigint NOT NULL DEFAULT '0', `creator` varchar(64) DEFAULT '', `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updater` varchar(64) DEFAULT '', `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `deleted` bit(1) NOT NULL DEFAULT b'0', PRIMARY KEY (`id`), KEY `idx_user_id` (`user_id`), KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI 学习计划';

-- ============================================
-- 初始化数据（可选）
-- ============================================
INSERT IGNORE INTO ai_subject_category (name, code, parent_id, sort, status) VALUES
('高等数学', 'MATH', 0, 1, 0),
('大学英语', 'ENGLISH', 0, 2, 0),
('计算机专业课', 'COMPUTER', 0, 3, 0),
('思政课', 'POLITICS', 0, 4, 0);
