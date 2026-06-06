-- AI 教育管理菜单初始化
-- 编码: UTF-8

SET NAMES utf8mb4;

-- 一级目录
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, component, icon, status, visible, keep_alive, creator, updater, create_time, update_time, deleted)
VALUES ('AI 教育管理', '', 1, 20, 2758, '/ai/education', '', 'ep:school', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0);
SET @menu = LAST_INSERT_ID();

-- 二级页面
INSERT INTO system_menu (name, permission, type, sort, parent_id, path, component, icon, status, visible, keep_alive, creator, updater, create_time, update_time, deleted) VALUES
('学科分类', 'ai:subject-category:query', 2, 1, @menu, 'subject-category', 'ai/education/subjectCategory/index', 'ep:collection', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('知识点标签', 'ai:knowledge-tag:query', 2, 2, @menu, 'knowledge-tag', 'ai/education/knowledgeTag/index', 'ep:price-tag', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('题库管理', 'ai:question-bank:query', 2, 3, @menu, 'question-bank', 'ai/education/questionBank/index', 'ep:document', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('作业管理', 'ai:homework:query', 2, 4, @menu, 'homework', 'ai/education/homework/index', 'ep:notebook', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('模拟考试', 'ai:exam:query', 2, 5, @menu, 'exam', 'ai/education/exam/index', 'ep:edit-pen', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('错题管理', 'ai:wrong-answer:query', 2, 6, @menu, 'wrong-answer', 'ai/education/wrongAnswer/index', 'ep:warning', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('学习计划', 'ai:study-plan:query', 2, 7, @menu, 'study-plan', 'ai/education/studyPlan/index', 'ep:clock', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('消息通知', 'ai:notification:query', 2, 8, @menu, 'notification', 'ai/education/notification/index', 'ep:bell', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('AI 配置', 'ai:education-config:query', 2, 9, @menu, 'config', 'ai/education/config/index', 'ep:setting', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0);

-- 按钮权限
SET @s1 = (SELECT id FROM system_menu WHERE path='subject-category' AND parent_id=@menu);
SET @s2 = (SELECT id FROM system_menu WHERE path='knowledge-tag' AND parent_id=@menu);
SET @s3 = (SELECT id FROM system_menu WHERE path='question-bank' AND parent_id=@menu);
SET @s4 = (SELECT id FROM system_menu WHERE path='homework' AND parent_id=@menu);
SET @s5 = (SELECT id FROM system_menu WHERE path='exam' AND parent_id=@menu);
SET @s8 = (SELECT id FROM system_menu WHERE path='notification' AND parent_id=@menu);
SET @s9 = (SELECT id FROM system_menu WHERE path='config' AND parent_id=@menu);

INSERT INTO system_menu (name, permission, type, sort, parent_id, path, component, icon, status, visible, keep_alive, creator, updater, create_time, update_time, deleted) VALUES
('创建', 'ai:subject-category:create', 3, 1, @s1, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('修改', 'ai:subject-category:update', 3, 2, @s1, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('删除', 'ai:subject-category:delete', 3, 3, @s1, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('创建', 'ai:knowledge-tag:create', 3, 1, @s2, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('修改', 'ai:knowledge-tag:update', 3, 2, @s2, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('删除', 'ai:knowledge-tag:delete', 3, 3, @s2, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('创建', 'ai:question-bank:create', 3, 1, @s3, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('修改', 'ai:question-bank:update', 3, 2, @s3, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('删除', 'ai:question-bank:delete', 3, 3, @s3, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('创建', 'ai:homework:create', 3, 1, @s4, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('修改', 'ai:homework:update', 3, 2, @s4, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('删除', 'ai:homework:delete', 3, 3, @s4, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('创建', 'ai:exam:create', 3, 1, @s5, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('修改', 'ai:exam:update', 3, 2, @s5, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('删除', 'ai:exam:delete', 3, 3, @s5, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('发送', 'ai:notification:create', 3, 1, @s8, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0),
('修改', 'ai:education-config:update', 3, 1, @s9, '', '', '', 0, 1, 1, 'admin', 'admin', NOW(), NOW(), 0);
