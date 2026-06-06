-- ========================================
-- AI 模块表添加 tenant_id 列
-- ========================================

ALTER TABLE `ai_api_key`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_model`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_chat_role`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_tool`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_chat_conversation`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_chat_message`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_knowledge`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_knowledge_document`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_knowledge_segment`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_image`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_music`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_mind_map`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_write`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_workflow`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_tutoring_session`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_tutoring_message`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_student_profile`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_learning_path`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_learning_path_node`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_learning_resource`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

ALTER TABLE `ai_learning_evaluation`
    ADD COLUMN `tenant_id` bigint NOT NULL DEFAULT '0' COMMENT '租户编号'
    AFTER `deleted`;

-- ========================================
-- 如已有租户数据，更新 tenant_id 为默认值
-- ========================================
UPDATE `ai_api_key` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_model` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_chat_role` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_tool` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_chat_conversation` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_chat_message` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_knowledge` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_knowledge_document` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_knowledge_segment` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_image` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_music` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_mind_map` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_write` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_workflow` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_tutoring_session` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_tutoring_message` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_student_profile` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_learning_path` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_learning_path_node` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_learning_resource` SET `tenant_id` = 1 WHERE `tenant_id` = 0;
UPDATE `ai_learning_evaluation` SET `tenant_id` = 1 WHERE `tenant_id` = 0;

-- ========================================
-- 学生画像：添加对话历史字段
-- ========================================
ALTER TABLE `ai_student_profile`
    ADD COLUMN `conversation_history` longtext COMMENT '对话历史(JSON数组)'
    AFTER `profile_json`;

-- ========================================
-- ai_model 添加模型参数字段
-- ========================================
ALTER TABLE `ai_model`
    ADD COLUMN `top_p` double DEFAULT NULL COMMENT 'TopP 核采样参数'
    AFTER `temperature`;

ALTER TABLE `ai_model`
    ADD COLUMN `frequency_penalty` double DEFAULT NULL COMMENT '频率惩罚（-2.0到2.0）'
    AFTER `top_p`;

ALTER TABLE `ai_model`
    ADD COLUMN `presence_penalty` double DEFAULT NULL COMMENT '存在惩罚（-2.0到2.0）'
    AFTER `frequency_penalty`;
