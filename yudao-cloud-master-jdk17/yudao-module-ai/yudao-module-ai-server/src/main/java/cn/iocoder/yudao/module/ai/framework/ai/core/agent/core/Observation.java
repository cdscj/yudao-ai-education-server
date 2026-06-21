package cn.iocoder.yudao.module.ai.framework.ai.core.agent.core;

import lombok.Data;

import java.util.List;

/**
 * ReAct Agent 的思考-行动-观察三元素
 *
 * @author yudao
 */
@Data
public class Observation {

    /** 步骤编号 */
    private int step;

    /** 思考内容 */
    private String thought;

    /** 选择的行动 */
    private Action action;

    /** 观察结果 */
    private String observation;

    @Data
    public static class Action {
        /** 工具名称 */
        private String toolName;
        /** 工具输入参数 */
        private String toolInput;
    }

    public static Observation of(int step, String thought) {
        Observation obs = new Observation();
        obs.setStep(step);
        obs.setThought(thought);
        return obs;
    }

    public boolean isFinal() {
        return action == null || "final_answer".equals(action.getToolName());
    }
}
