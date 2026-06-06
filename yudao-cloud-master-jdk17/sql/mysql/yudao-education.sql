-- 学生画像表
CREATE TABLE IF NOT EXISTS i_student_profile (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    user_id bigint NOT NULL COMMENT '用户编号',
    major varchar(100) DEFAULT NULL COMMENT '专业',
    grade varchar(50) DEFAULT NULL COMMENT '年级',
    learning_goals varchar(500) DEFAULT NULL COMMENT '学习目标',
    knowledge_level varchar(200) DEFAULT NULL COMMENT '知识水平',
    learning_preferences varchar(200) DEFAULT NULL COMMENT '学习偏好',
    weak_points varchar(500) DEFAULT NULL COMMENT '薄弱环节',
    strong_points varchar(500) DEFAULT NULL COMMENT '优势能力',
    study_history text COMMENT '学习经历',
    learning_speed int DEFAULT NULL COMMENT '学习速度(1-5)',
    preferred_resource_types varchar(200) DEFAULT NULL COMMENT '偏好资源类型',
    study_time_preference varchar(100) DEFAULT NULL COMMENT '学习时间偏好',
    status varchar(20) DEFAULT 'ACTIVE' COMMENT '状态',
    profile_json text COMMENT '画像JSON数据',
    creator varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_user_id (user_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学生画像';

-- 学习资源表
CREATE TABLE IF NOT EXISTS i_learning_resource (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    user_id bigint NOT NULL COMMENT '用户编号',
    profile_id bigint DEFAULT NULL COMMENT '画像编号',
    	itle varchar(200) DEFAULT NULL COMMENT '标题',
    esource_type varchar(50) NOT NULL COMMENT '资源类型(DOCUMENT/MIND_MAP/EXERCISE/READING/CODE_EXAMPLE)',
    content longtext COMMENT '内容',
    content_json text COMMENT '内容JSON',
    	ags varchar(500) DEFAULT NULL COMMENT '标签',
    difficulty varchar(20) DEFAULT NULL COMMENT '难度',
    elated_course_id bigint DEFAULT NULL COMMENT '关联课程编号',
    course_name varchar(200) DEFAULT NULL COMMENT '课程名称',
    status varchar(20) DEFAULT 'GENERATING' COMMENT '状态',
    error_message varchar(2000) DEFAULT NULL COMMENT '错误信息',
    progress int DEFAULT '0' COMMENT '学习进度',
    eedback varchar(500) DEFAULT NULL COMMENT '反馈',
    creator varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_user_id (user_id) USING BTREE,
    KEY idx_resource_type (esource_type) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习资源';

-- 学习路径表
CREATE TABLE IF NOT EXISTS i_learning_path (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    user_id bigint NOT NULL COMMENT '用户编号',
    profile_id bigint DEFAULT NULL COMMENT '画像编号',
    	itle varchar(200) DEFAULT NULL COMMENT '标题',
    goal varchar(500) DEFAULT NULL COMMENT '学习目标',
    description text COMMENT '路径描述',
    	otal_nodes int DEFAULT '0' COMMENT '节点总数',
    completed_nodes int DEFAULT '0' COMMENT '已完成节点数',
    status varchar(20) DEFAULT 'GENERATING' COMMENT '状态',
    start_time datetime DEFAULT NULL COMMENT '开始时间',
    expected_end_time datetime DEFAULT NULL COMMENT '预计完成时间',
    completed_time datetime DEFAULT NULL COMMENT '完成时间',
    creator varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_user_id (user_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习路径';

-- 学习路径节点表
CREATE TABLE IF NOT EXISTS i_learning_path_node (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    path_id bigint NOT NULL COMMENT '路径编号',
    user_id bigint DEFAULT NULL COMMENT '用户编号',
    	itle varchar(200) DEFAULT NULL COMMENT '标题',
    description text COMMENT '描述',
    content text COMMENT '内容',
    sort_order int DEFAULT NULL COMMENT '排序',
    status varchar(20) DEFAULT 'PENDING' COMMENT '状态',
    start_time datetime DEFAULT NULL COMMENT '开始时间',
    completed_time datetime DEFAULT NULL COMMENT '完成时间',
    creator varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_path_id (path_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习路径节点';

-- 辅导会话表
CREATE TABLE IF NOT EXISTS i_tutoring_session (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    user_id bigint NOT NULL COMMENT '用户编号',
    	itle varchar(200) DEFAULT NULL COMMENT '标题',
    question text COMMENT '初始问题',
    context_json text COMMENT '上下文JSON',
    status varchar(20) DEFAULT 'ACTIVE' COMMENT '状态',
    creator varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_user_id (user_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 辅导会话';

-- 辅导消息表
CREATE TABLE IF NOT EXISTS i_tutoring_message (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    session_id bigint NOT NULL COMMENT '会话编号',
    user_id bigint DEFAULT NULL COMMENT '用户编号',
    ole varchar(20) NOT NULL COMMENT '角色(user/assistant)',
    content_type varchar(20) DEFAULT 'text' COMMENT '内容类型',
    content longtext COMMENT '内容',
    content_json text COMMENT '内容JSON',
    creator varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_session_id (session_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 辅导消息';

-- 学习评估表
CREATE TABLE IF NOT EXISTS i_learning_evaluation (
    id bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
    user_id bigint NOT NULL COMMENT '用户编号',
    profile_id bigint DEFAULT NULL COMMENT '画像编号',
    evaluation_type varchar(50) DEFAULT NULL COMMENT '评估类型',
    dimension varchar(50) DEFAULT NULL COMMENT '评估维度',
    score int DEFAULT NULL COMMENT '得分',
    max_score int DEFAULT '100' COMMENT '满分',
    evaluation text COMMENT '评估内容',
    suggestion text COMMENT '改进建议',
    evaluation_json text COMMENT '评估JSON',
    creator varchar(64) DEFAULT '' COMMENT '创建者',
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updater varchar(64) DEFAULT '' COMMENT '更新者',
    update_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted bit(1) NOT NULL DEFAULT b'0' COMMENT '是否删除',
    PRIMARY KEY (id) USING BTREE,
    KEY idx_user_id (user_id) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI 学习评估';
