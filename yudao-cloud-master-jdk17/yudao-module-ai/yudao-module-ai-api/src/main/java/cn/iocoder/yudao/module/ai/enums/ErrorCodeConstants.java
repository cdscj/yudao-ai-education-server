package cn.iocoder.yudao.module.ai.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * AI 错误码枚举类
 * <p>
 * ai 系统，使用 1-040-000-000 段
 */
public interface ErrorCodeConstants {

    // ========== API 密钥 1-040-000-000 ==========
    ErrorCode API_KEY_NOT_EXISTS = new ErrorCode(1_040_000_000, "API 密钥不存在");
    ErrorCode API_KEY_DISABLE = new ErrorCode(1_040_000_001, "API 密钥已禁用！");

    // ========== API 模型 1-040-001-000 ==========
    ErrorCode MODEL_NOT_EXISTS = new ErrorCode(1_040_001_000, "模型不存在!");
    ErrorCode MODEL_DISABLE = new ErrorCode(1_040_001_001, "模型({})已禁用!");
    ErrorCode MODEL_DEFAULT_NOT_EXISTS = new ErrorCode(1_040_001_002, "操作失败，找不到默认模型");
    ErrorCode MODEL_USE_TYPE_ERROR = new ErrorCode(1_040_001_003, "操作失败，该模型的模型类型不正确");

    // ========== API 聊天角色 1-040-002-000 ==========
    ErrorCode CHAT_ROLE_NOT_EXISTS = new ErrorCode(1_040_002_000, "聊天角色不存在");
    ErrorCode CHAT_ROLE_DISABLE = new ErrorCode(1_040_002_001, "聊天角色({})已禁用!");

    // ========== API 聊天会话 1-040-003-000 ==========
    ErrorCode CHAT_CONVERSATION_NOT_EXISTS = new ErrorCode(1_040_003_000, "对话不存在!");
    ErrorCode CHAT_CONVERSATION_MODEL_ERROR = new ErrorCode(1_040_003_001, "操作失败，该聊天模型的配置不完整");

    // ========== API 聊天消息 1-040-004-000 ==========
    ErrorCode CHAT_MESSAGE_NOT_EXIST = new ErrorCode(1_040_004_000, "消息不存在!");
    ErrorCode CHAT_STREAM_ERROR = new ErrorCode(1_040_004_001, "对话生成异常!");

    // ========== API 绘画 1-040-005-000 ==========
    ErrorCode IMAGE_NOT_EXISTS = new ErrorCode(1_040_005_000, "图片不存在!");
    ErrorCode IMAGE_MIDJOURNEY_SUBMIT_FAIL = new ErrorCode(1_040_005_001, "Midjourney 提交失败!原因：{}");
    ErrorCode IMAGE_CUSTOM_ID_NOT_EXISTS = new ErrorCode(1_040_005_002, "Midjourney 按钮 customId 不存在! {}");

    // ========== API 音乐 1-040-006-000 ==========
    ErrorCode MUSIC_NOT_EXISTS = new ErrorCode(1_040_006_000, "音乐不存在!");

    // ========== API 写作 1-040-007-000 ==========
    ErrorCode WRITE_NOT_EXISTS = new ErrorCode(1_040_007_000, "作文不存在!");
    ErrorCode WRITE_STREAM_ERROR = new ErrorCode(1_040_07_001, "写作生成异常!");

    // ========== API 思维导图 1-040-008-000 ==========
    ErrorCode MIND_MAP_NOT_EXISTS = new ErrorCode(1_040_008_000, "思维导图不存在!");

    // ========== API 知识库 1-040-009-000 ==========
    ErrorCode KNOWLEDGE_NOT_EXISTS = new ErrorCode(1_040_009_000, "知识库不存在!");

    ErrorCode KNOWLEDGE_DOCUMENT_NOT_EXISTS = new ErrorCode(1_040_009_101, "文档不存在!");
    ErrorCode KNOWLEDGE_DOCUMENT_FILE_EMPTY = new ErrorCode(1_040_009_102, "文档内容为空!");
    ErrorCode KNOWLEDGE_DOCUMENT_FILE_DOWNLOAD_FAIL = new ErrorCode(1_040_009_104, "文件下载失败!");
    ErrorCode KNOWLEDGE_DOCUMENT_FILE_READ_FAIL = new ErrorCode(1_040_009_105, "文档加载失败!");
    ErrorCode KNOWLEDGE_DOCUMENT_FILE_UPLOAD_FAIL = new ErrorCode(1_040_009_103, "文件上传失败!");

    ErrorCode KNOWLEDGE_SEGMENT_NOT_EXISTS = new ErrorCode(1_040_009_202, "段落不存在!");
    ErrorCode KNOWLEDGE_SEGMENT_CONTENT_TOO_LONG = new ErrorCode(1_040_009_203, "内容 Token 数为 {}，超过最大限制 {}");

    // ========== AI 工具 1-040-010-000 ==========
    ErrorCode TOOL_NOT_EXISTS = new ErrorCode(1_040_010_000, "工具不存在");
    ErrorCode TOOL_NAME_NOT_EXISTS = new ErrorCode(1_040_010_001, "工具({})找不到 Bean");

    // ========== AI 工作流 1-040-011-000 ==========
    ErrorCode WORKFLOW_NOT_EXISTS = new ErrorCode(1_040_011_000, "工作流不存在");
    ErrorCode WORKFLOW_CODE_EXISTS = new ErrorCode(1_040_011_001, "工作流标识已存在");

    // ========== 个性化教育 1-040-012-000 ==========
    ErrorCode STUDENT_PROFILE_NOT_EXISTS = new ErrorCode(1_040_012_000, "学生画像不存在");
    ErrorCode LEARNING_RESOURCE_NOT_EXISTS = new ErrorCode(1_040_012_001, "学习资源不存在");
    ErrorCode LEARNING_PATH_NOT_EXISTS = new ErrorCode(1_040_012_002, "学习路径不存在");
    ErrorCode LEARNING_PATH_NODE_NOT_EXISTS = new ErrorCode(1_040_012_003, "学习路径节点不存在");
    ErrorCode TUTORING_SESSION_NOT_EXISTS = new ErrorCode(1_040_012_004, "辅导会话不存在");
    ErrorCode TUTORING_MESSAGE_NOT_EXISTS = new ErrorCode(1_040_012_005, "辅导消息不存在");
    ErrorCode EVALUATION_NOT_EXISTS = new ErrorCode(1_040_012_006, "学习评估不存在");
    ErrorCode EDUCATION_STREAM_ERROR = new ErrorCode(1_040_012_007, "AI 教育生成异常");

    // ========== 提示词模板 1-040-013-000 ==========
    ErrorCode PROMPT_TEMPLATE_NOT_EXISTS = new ErrorCode(1_040_013_000, "提示词模板不存在");
    ErrorCode PROMPT_TEMPLATE_NAME_EXISTS = new ErrorCode(1_040_013_001, "提示词模板名称已存在");

    // ========== 敏感词 1-040-014-000 ==========
    ErrorCode SENSITIVE_WORD_NOT_EXISTS = new ErrorCode(1_040_014_000, "敏感词不存在");
    ErrorCode SENSITIVE_WORD_EXISTS = new ErrorCode(1_040_014_001, "敏感词已存在");
    ErrorCode SENSITIVE_WORD_DETECTED = new ErrorCode(1_040_014_002, "内容包含敏感词，请修改后重试");

    // ========== API 统计 1-040-015-000 ==========
    ErrorCode STATISTICS_NOT_EXISTS = new ErrorCode(1_040_015_000, "统计数据不存在");

    // ========== 用户配额 1-040-016-000 ==========
    ErrorCode USER_QUOTA_NOT_EXISTS = new ErrorCode(1_040_016_000, "用户配额配置不存在");
    ErrorCode USER_QUOTA_DAILY_EXCEEDED = new ErrorCode(1_040_016_001, "今日调用次数已用完，请明天再试");
    ErrorCode USER_QUOTA_MONTHLY_EXCEEDED = new ErrorCode(1_040_016_002, "本月调用次数已用完");
    ErrorCode USER_QUOTA_TOTAL_EXCEEDED = new ErrorCode(1_040_016_003, "总调用次数已用完");
    ErrorCode USER_QUOTA_DISABLED = new ErrorCode(1_040_016_004, "您的AI调用权限已被禁用");

    // ========== 对话日志 1-040-017-000 ==========
    ErrorCode CONVERSATION_LOG_NOT_EXISTS = new ErrorCode(1_040_017_000, "对话日志不存在");

    // ========== 系统配置 1-040-018-000 ==========
    ErrorCode SYSTEM_CONFIG_NOT_EXISTS = new ErrorCode(1_040_018_000, "系统配置不存在");
    ErrorCode SYSTEM_CONFIG_KEY_EXISTS = new ErrorCode(1_040_018_001, "系统配置键已存在");

    // ========== 签到 1-040-019-000 ==========
    ErrorCode CHECK_IN_TODAY_EXISTS = new ErrorCode(1_040_019_000, "今日已签到，请明天再来");
    ErrorCode CHECK_IN_RECORD_NOT_EXISTS = new ErrorCode(1_040_019_001, "签到记录不存在");

    // ========== 积分 1-040-020-000 ==========
    ErrorCode USER_POINTS_NOT_EXISTS = new ErrorCode(1_040_020_000, "用户积分记录不存在");
    ErrorCode POINTS_INSUFFICIENT = new ErrorCode(1_040_020_001, "积分不足");

    // ========== 好友 1-040-021-000 ==========
    ErrorCode FRIEND_REQUEST_EXISTS = new ErrorCode(1_040_021_000, "已发送过好友申请");
    ErrorCode FRIEND_NOT_EXISTS = new ErrorCode(1_040_021_001, "好友关系不存在");
    ErrorCode FRIEND_ALREADY_ACCEPTED = new ErrorCode(1_040_021_002, "已是好友关系");
    ErrorCode FRIEND_BLOCKED = new ErrorCode(1_040_021_003, "对方已将你拉黑");
    ErrorCode FRIEND_SELF = new ErrorCode(1_040_021_004, "不能添加自己为好友");

    // ========== 关注 1-040-022-000 ==========
    ErrorCode FOLLOW_NOT_EXISTS = new ErrorCode(1_040_022_000, "关注关系不存在");
    ErrorCode FOLLOW_SELF = new ErrorCode(1_040_022_001, "不能关注自己");

    // ========== 排行榜 1-040-023-000 ==========
    ErrorCode LEADERBOARD_NOT_EXISTS = new ErrorCode(1_040_023_000, "排行榜记录不存在");

    // ========== 学习目标 1-040-024-000 ==========
    ErrorCode LEARNING_GOAL_NOT_EXISTS = new ErrorCode(1_040_024_000, "学习目标不存在");
    ErrorCode LEARNING_GOAL_ALREADY_COMPLETED = new ErrorCode(1_040_024_001, "目标已完成");

    // ========== 用户动态 1-040-025-000 ==========
    ErrorCode USER_ACTIVITY_NOT_EXISTS = new ErrorCode(1_040_025_000, "用户动态不存在");

    // ========== 学校 1-040-026-000 ==========
    ErrorCode SCHOOL_NOT_EXISTS = new ErrorCode(1_040_026_000, "学校不存在");
    ErrorCode SCHOOL_NAME_EXISTS = new ErrorCode(1_040_026_001, "学校名称已存在");

    // ========== 学生学校 1-040-027-000 ==========
    ErrorCode STUDENT_SCHOOL_NOT_EXISTS = new ErrorCode(1_040_027_000, "学生学校信息不存在");
    ErrorCode STUDENT_SCHOOL_EXISTS = new ErrorCode(1_040_027_001, "学生已绑定学校");

    // ========== 课程表 1-040-028-000 ==========
    ErrorCode COURSE_NOT_EXISTS = new ErrorCode(1_040_028_000, "课程不存在");

    // ========== 学科分类 1-040-029-000 ==========
    ErrorCode SUBJECT_CATEGORY_NOT_EXISTS = new ErrorCode(1_040_029_000, "学科分类不存在");
    ErrorCode SUBJECT_CATEGORY_CODE_EXISTS = new ErrorCode(1_040_029_001, "学科编码已存在");
    ErrorCode SUBJECT_CATEGORY_HAS_CHILDREN = new ErrorCode(1_040_029_002, "该分类下有子分类，无法删除");

    // ========== 知识点标签 1-040-029-100 ==========
    ErrorCode KNOWLEDGE_TAG_NOT_EXISTS = new ErrorCode(1_040_029_100, "知识点标签不存在");

    // ========== 题库 1-040-030-000 ==========
    ErrorCode QUESTION_NOT_EXISTS = new ErrorCode(1_040_030_000, "题目不存在");
    ErrorCode QUESTION_AI_GENERATE_FAIL = new ErrorCode(1_040_030_001, "AI 生成题目失败");

    // ========== 错题本 1-040-031-000 ==========
    ErrorCode WRONG_ANSWER_NOT_EXISTS = new ErrorCode(1_040_031_000, "错题记录不存在");
    ErrorCode WRONG_ANSWER_ALREADY_RECORDED = new ErrorCode(1_040_031_001, "该题目已记录");

    // ========== 作业 1-040-032-000 ==========
    ErrorCode HOMEWORK_NOT_EXISTS = new ErrorCode(1_040_032_000, "作业不存在");
    ErrorCode HOMEWORK_ALREADY_SUBMITTED = new ErrorCode(1_040_032_001, "作业已提交");
    ErrorCode HOMEWORK_DEADLINE_PASSED = new ErrorCode(1_040_032_002, "作业已过截止时间");
    ErrorCode HOMEWORK_NOT_PUBLISHED = new ErrorCode(1_040_032_003, "作业未发布");
    ErrorCode HOMEWORK_SUBMISSION_NOT_EXISTS = new ErrorCode(1_040_032_004, "提交记录不存在");
    ErrorCode HOMEWORK_AI_GRADE_FAIL = new ErrorCode(1_040_032_005, "AI 批改失败");

    // ========== 模拟考试 1-040-033-000 ==========
    ErrorCode EXAM_NOT_EXISTS = new ErrorCode(1_040_033_000, "考试不存在");
    ErrorCode EXAM_NOT_PUBLISHED = new ErrorCode(1_040_033_001, "考试未发布");
    ErrorCode EXAM_EXPIRED = new ErrorCode(1_040_033_002, "考试已结束");
    ErrorCode EXAM_RECORD_NOT_EXISTS = new ErrorCode(1_040_033_003, "考试记录不存在");
    ErrorCode EXAM_ALREADY_SUBMITTED = new ErrorCode(1_040_033_004, "已交卷");
    ErrorCode EXAM_AI_GENERATE_FAIL = new ErrorCode(1_040_033_005, "AI 组卷失败");

    // ========== 学习数据 1-040-034-000 ==========
    ErrorCode STUDY_DATA_NOT_AVAILABLE = new ErrorCode(1_040_034_000, "学习数据暂不可用");

    // ========== 消息通知 1-040-035-000 ==========
    ErrorCode NOTIFICATION_NOT_EXISTS = new ErrorCode(1_040_035_000, "通知不存在");

    // ========== AI 教育配置 1-040-036-000 ==========
    ErrorCode EDU_CONFIG_NOT_EXISTS = new ErrorCode(1_040_036_000, "教育配置不存在");

    // ========== 学习计划 1-040-037-000 ==========
    ErrorCode STUDY_PLAN_NOT_EXISTS = new ErrorCode(1_040_037_000, "学习计划不存在");
    ErrorCode STUDY_PLAN_ALREADY_ACTIVE = new ErrorCode(1_040_037_001, "已有进行中的计划");
    ErrorCode STUDY_PLAN_AI_GENERATE_FAIL = new ErrorCode(1_040_037_002, "AI 生成学习计划失败");
}
