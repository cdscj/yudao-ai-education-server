package cn.iocoder.yudao.module.ai.enums.agent;

import cn.iocoder.yudao.framework.common.core.IntArrayValuable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * AI Agent 类型枚举
 *
 * <p>定义了平台支持的所有智能体类型，每种类型对应一个专用的 AI Agent：
 * <ul>
 *   <li>对话型 Agent（CODE, RESEARCH, WRITE, TUTOR, GENERAL）：通过对话与用户交互</li>
 *   <li>教育型 Agent（PROFILE, RESOURCE, PATH）：服务于教育场景的专用智能体</li>
 *   <li>系统型 Agent（MEMORY）：提供底层支撑能力</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
@Getter
@AllArgsConstructor
public enum AiAgentTypeEnum implements IntArrayValuable {

    /** 代码智能体 - 编程、调试、算法讲解 */
    CODE(1, "code", "代码智能体"),
    /** 研究智能体 - 深度调研、资料整理、报告生成 */
    RESEARCH(2, "research", "研究智能体"),
    /** 写作智能体 - 文案撰写、翻译、润色 */
    WRITE(3, "write", "写作智能体"),
    /** 答疑智能体 - 课程辅导、知识点讲解 */
    TUTOR(4, "tutor", "答疑智能体"),
    /** 通用智能体 - 处理未分类的通用问题 */
    GENERAL(5, "general", "通用智能体"),
    /** 画像智能体 - 学生画像构建 */
    PROFILE(6, "profile", "画像智能体"),
    /** 资源生成智能体 - 多模态学习资源生成 */
    RESOURCE(7, "resource", "资源生成智能体"),
    /** 路径规划智能体 - 个性化学习路径规划 */
    PATH(8, "path", "路径规划智能体"),
    /** 记忆压缩智能体 - 上下文压缩与摘要 */
    MEMORY(9, "memory", "记忆压缩智能体");

    /** 类型编号 */
    private final Integer type;
    /** 类型编码（对应 Agent Bean 的 agentId 后缀） */
    private final String code;
    /** 类型名称 */
    private final String name;

    public static final int[] ARRAYS = Arrays.stream(values())
            .mapToInt(AiAgentTypeEnum::getType).toArray();

    @Override
    public int[] array() {
        return ARRAYS;
    }

    /**
     * 根据类型编号获取枚举
     *
     * @param type 类型编号
     * @return 对应的枚举值
     */
    public static AiAgentTypeEnum getByType(Integer type) {
        return Arrays.stream(values())
                .filter(e -> e.getType().equals(type))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据编码获取枚举
     *
     * @param code 类型编码
     * @return 对应的枚举值
     */
    public static AiAgentTypeEnum getByCode(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }
}
