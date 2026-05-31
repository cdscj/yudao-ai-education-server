package cn.iocoder.yudao.module.ai.framework.ai.core.agent;

import reactor.core.publisher.Flux;

public interface AiAgent {

    String getAgentId();

    String getAgentName();

    Flux<String> execute(String input, String context);

    Flux<String> executeStream(String input, String context);
}
