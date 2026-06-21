package cn.iocoder.yudao.module.ai.framework.ai.core.agent.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Agent 工作记忆 — 在单次 Agent 执行中存储中间步骤
 *
 * @author yudao
 */
public class AgentMemory {

    private final List<Observation> observations = new ArrayList<>();
    private String finalAnswer;

    public void addObservation(Observation obs) {
        observations.add(obs);
    }

    public List<Observation> getObservations() {
        return new ArrayList<>(observations);
    }

    public String getContext() {
        StringBuilder sb = new StringBuilder();
        for (Observation obs : observations) {
            sb.append("\n步骤 ").append(obs.getStep()).append(":\n");
            sb.append("思考: ").append(obs.getThought()).append("\n");
            if (obs.getAction() != null) {
                sb.append("行动: ").append(obs.getAction().getToolName())
                        .append("(").append(obs.getAction().getToolInput()).append(")\n");
            }
            if (obs.getObservation() != null) {
                sb.append("观察: ").append(obs.getObservation()).append("\n");
            }
        }
        return sb.toString();
    }

    public String getFinalAnswer() { return finalAnswer; }
    public void setFinalAnswer(String finalAnswer) { this.finalAnswer = finalAnswer; }
    public int getStepCount() { return observations.size(); }
}
