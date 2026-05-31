package cn.iocoder.yudao.module.ai.framework.ai.core.agent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AiAgentTypeEnum {

    PROFILE("profile", "画像智能体"),
    RESOURCE("resource", "资源生成智能体"),
    PATH("path", "路径规划智能体"),
    TUTOR("tutor", "答疑智能体"),
    MEMORY("memory", "记忆压缩智能体");

    private final String code;
    private final String name;
}
