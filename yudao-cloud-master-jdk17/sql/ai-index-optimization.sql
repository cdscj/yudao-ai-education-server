-- =============================================
-- AI 模块性能优化索引
-- 为 13 张无索引核心表 + 其他高频查询表建立索引
-- =============================================

-- ========== 聊天相关（最高频） ==========
CREATE INDEX idx_conversation_id ON ai_chat_message (conversation_id);
CREATE INDEX idx_user_id ON ai_chat_message (user_id);
CREATE INDEX idx_user_id ON ai_chat_conversation (user_id);
CREATE INDEX idx_user_pinned ON ai_chat_conversation (user_id, pinned);

-- ========== 知识库相关（高频） ==========
CREATE INDEX idx_document_id ON ai_knowledge_segment (document_id);
CREATE INDEX idx_knowledge_status ON ai_knowledge_segment (knowledge_id, status);
CREATE INDEX idx_knowledge_id ON ai_knowledge_document (knowledge_id);
CREATE INDEX idx_segment_status ON ai_knowledge_document (status);
CREATE INDEX idx_knowledge_status ON ai_knowledge (status);

-- ========== AI 模型与密钥（每次调用都查） ==========
CREATE INDEX idx_type_status ON ai_model (type, status);
CREATE INDEX idx_platform_status ON ai_api_key (platform, status);

-- ========== 图片/音乐 ==========
CREATE INDEX idx_task_id ON ai_image (task_id);
CREATE INDEX idx_image_user_id ON ai_image (user_id);
CREATE INDEX idx_music_user_id ON ai_music (user_id);
CREATE INDEX idx_music_status ON ai_music (status);

-- ========== 智能辅导（高频学生端） ==========
CREATE INDEX idx_tutoring_session_user ON ai_tutoring_session (user_id);
CREATE INDEX idx_tutoring_message_session ON ai_tutoring_message (session_id);

-- ========== 学习路径/资源/评估 ==========
CREATE INDEX idx_learning_path_user ON ai_learning_path (user_id);
CREATE INDEX idx_learning_path_node_path ON ai_learning_path_node (path_id);
CREATE INDEX idx_learning_resource_user ON ai_learning_resource (user_id);
CREATE INDEX idx_learning_evaluation_user ON ai_learning_evaluation (user_id);

-- ========== 学生画像（唯一查询） ==========
CREATE UNIQUE INDEX uk_student_profile_user ON ai_student_profile (user_id);

-- ========== 聊天角色/工具/工作流/思维导图/写作 ==========
CREATE INDEX idx_chat_role_status ON ai_chat_role (status);
CREATE INDEX idx_tool_status ON ai_tool (status);
CREATE INDEX idx_workflow_code ON ai_workflow (code);
CREATE INDEX idx_workflow_status ON ai_workflow (status);
CREATE INDEX idx_mind_map_user ON ai_mind_map (user_id);
CREATE INDEX idx_write_user ON ai_write (user_id);

-- ========== 系统配置/提示词模板 ==========
CREATE INDEX idx_system_config_category ON ai_system_config (category);
CREATE INDEX idx_prompt_template_category ON ai_prompt_template (category, status);
