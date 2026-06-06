-- Task 2: Create school & course schedule tables
-- Run against ruoyi-vue-pro database

-- 35. AI 学校信息
CREATE TABLE IF NOT EXISTS `ai_school` (
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
CREATE TABLE IF NOT EXISTS `ai_student_school` (
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
CREATE TABLE IF NOT EXISTS `ai_course_schedule` (
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

-- 课程表增量 DDL（已有表执行）
-- ALTER TABLE ai_course_schedule ADD COLUMN school_id bigint DEFAULT NULL COMMENT '学校编号' AFTER user_id;
-- ALTER TABLE ai_course_schedule MODIFY COLUMN user_id bigint DEFAULT NULL COMMENT '用户编号';
-- ALTER TABLE ai_course_schedule ADD KEY idx_school_id (school_id);

-- Sample school data
INSERT INTO ai_school (name, province, city, type) VALUES
('清华大学', '北京', '北京', 'UNIVERSITY'),
('北京大学', '北京', '北京', 'UNIVERSITY'),
('浙江大学', '浙江', '杭州', 'UNIVERSITY'),
('上海交通大学', '上海', '上海', 'UNIVERSITY'),
('武汉大学', '湖北', '武汉', 'UNIVERSITY');
