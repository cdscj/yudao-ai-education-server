package cn.iocoder.yudao.module.ai.framework.ai.core.agent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiAgentTypeEnum {

    CODE("code", "代码智能体"),
    RESEARCH("research", "研究智能体"),
    WRITE("write", "写作智能体"),
    TUTOR("tutor", "答疑智能体"),
    GENERAL("general", "通用智能体"),
    PROFILE("profile", "画像智能体"),
    RESOURCE("resource", "资源生成智能体"),
    PATH("path", "路径规划智能体"),
    MEMORY("memory", "记忆压缩智能体");

    private final String code;
    private final String name;

    /**
     * 根据 code 获取枚举值
     */
    public static AiAgentTypeEnum getByCode(String code) {
        for (AiAgentTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return GENERAL;
    }
}
