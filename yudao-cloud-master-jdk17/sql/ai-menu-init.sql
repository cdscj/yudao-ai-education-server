-- AI 模块菜单初始化数据
-- 在 yudao-module-system 的菜单表中插入 AI 管理菜单

-- ========================================
-- AI 系统配置初始数据
-- ========================================
INSERT INTO `ai_system_config` (`config_key`, `config_value`, `value_type`, `description`, `category`, `sort`, `status`) VALUES
('chat.auto_generate_title', 'true', 'BOOLEAN', '是否自动生成对话标题', 'chat', 1, 0),
('chat.max_conversations_per_user', '100', 'INTEGER', '每用户最大对话数', 'chat', 2, 0),
('chat.default_temperature', '0.8', 'DOUBLE', '默认温度参数', 'chat', 3, 0),
('chat.default_max_tokens', '2048', 'INTEGER', '默认最大Token数', 'chat', 4, 0),
('chat.default_max_contexts', '10', 'INTEGER', '默认上下文轮数', 'chat', 5, 0),
('security.sensitive_word_enabled', 'true', 'BOOLEAN', '是否启用敏感词过滤', 'security', 1, 0),
('security.rate_limit_enabled', 'true', 'BOOLEAN', '是否启用限流', 'security', 2, 0),
('security.rate_limit_count_per_minute', '20', 'INTEGER', '每分钟限流次数', 'security', 3, 0),
('security.quota_enabled', 'true', 'BOOLEAN', '是否启用配额管理', 'security', 4, 0),
('statistics.record_enabled', 'true', 'BOOLEAN', '是否启用调用统计', 'statistics', 1, 0),
('statistics.retention_days', '90', 'INTEGER', '统计数据保留天数', 'statistics', 2, 0);
