package cn.iocoder.yudao.module.ai.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SSE 流式端到端验证（需服务运行中）
 *
 * <p>启动 yudao-server 后运行此测试，观察控制台输出：
 * 每个 SSE 事件的到达时间和内容，验证是否为真正的流式传输。</p>
 *
 * <p>运行前提：
 * <ol>
 *   <li>yudao-server 已启动 (localhost:48080)</li>
 *   <li>local profile: mock 登录已启用</li>
 *   <li>已创建对话和默认模型</li>
 * </ol>
 * </p>
 */
@Slf4j
public class SseStreamVerificationTest {

    private static final String BASE_URL = "http://localhost:48080/admin-api/ai/chat/message/send-stream-plain";

    /**
     * 手动运行验证：直接通过 HTTP 连接消费 SSE 流
     *
     * <p>预期输出（控制台）：
     * <pre>
     * [00:00.000] 收到 metadata: {"sendId":1,...}
     * [00:01.234] 收到 chunk: 你
     * [00:01.456] 收到 chunk: 好
     * [00:01.678] 收到 chunk: ，
     * [00:01.890] 收到 chunk: 世界
     * [00:01.901] 收到 end: [DONE]
     * ✅ 流式验证通过！chunk 逐步到达证明是真正的流式传输
     * </pre>
     *
     * <p>如果是批量返回（非流式），所有 chunk 的时间戳会在同一毫秒内。</p>
     */
    @Test
    public void manualVerifySseStream() throws Exception {
        log.info("===============================================");
        log.info("SSE 流式验证 - 开始");
        log.info("时间: {}", LocalDateTime.now());
        log.info("===============================================");

        URL url = URI.create(BASE_URL).toURL();
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setDoOutput(true);

        // 模拟请求 body（需要根据实际数据库调整 conversationId）
        String body = """
                {
                    "conversationId": 1,
                    "content": "你好，请用一句话介绍你自己",
                    "useContext": true,
                    "useSearch": false
                }
                """;
        conn.getOutputStream().write(body.getBytes());
        conn.getOutputStream().flush();

        // 读取 SSE 流
        int responseCode = conn.getResponseCode();
        log.info("[响应] HTTP {}", responseCode);
        log.info("[响应] Content-Type: {}", conn.getContentType());

        // 断言 1：Content-Type 必须是 text/event-stream
        String contentType = conn.getContentType();
        assert contentType != null && contentType.contains("text/event-stream")
                : "❌ Content-Type 不是 text/event-stream！当前值: " + contentType;
        log.info("✅ Content-Type 正确: {}", contentType);

        // 逐行读取 SSE 事件
        List<SseEvent> events = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            StringBuilder dataBuffer = new StringBuilder();
            String currentEvent = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("event:")) {
                    currentEvent = line.substring(6).trim();
                } else if (line.startsWith("data:")) {
                    String data = line.substring(5).trim();
                    long elapsed = System.currentTimeMillis() - startTime;

                    if (!data.isEmpty() || currentEvent != null) {
                        SseEvent event = new SseEvent(currentEvent, data, elapsed);
                        events.add(event);
                        log.info("[+{:04d}ms] event={}, data={}",
                                elapsed,
                                currentEvent != null ? currentEvent : "chunk",
                                data.length() > 80 ? data.substring(0, 80) + "..." : data);
                    }

                    currentEvent = null;
                }
                // 空行 = 事件结束
            }
        }

        // === 断言 2：收到了事件 ===
        assert !events.isEmpty() : "❌ 没有收到任何 SSE 事件！";
        log.info("✅ 收到 {} 个 SSE 事件", events.size());

        // === 断言 3：第一个事件是 metadata JSON ===
        String firstData = events.get(0).data;
        assert firstData.startsWith("{") && firstData.contains("sendId")
                : "❌ 首事件不是 metadata JSON: " + firstData;
        log.info("✅ 首事件为 metadata: {}", firstData.substring(0, Math.min(80, firstData.length())));

        // === 断言 4：最后一个事件是 [DONE] ===
        String lastData = events.get(events.size() - 1).data;
        assert "[DONE]".equals(lastData)
                : "❌ 末事件不是 [DONE]: " + lastData;
        log.info("✅ 末事件为 [DONE]");

        // === 断言 5：chunk 是逐步到达的（核心验证）===
        if (events.size() >= 3) {
            long firstTs = events.get(0).elapsed;
            long lastTs = events.get(events.size() - 1).elapsed;
            long totalDuration = lastTs - firstTs;

            log.info("[流式统计] 事件数={}, 总耗时={}ms, 平均间隔={}ms",
                    events.size(), totalDuration,
                    events.size() > 1 ? totalDuration / (events.size() - 1) : 0);

            assert totalDuration > 0
                    : "❌ 所有 chunk 在同一毫秒到达，这不是流式传输！";
            log.info("✅ 事件逐步到达（{}ms），确认是真正的流式传输", totalDuration);
        }

        log.info("===============================================");
        log.info("✅ SSE 流式验证全部通过！");
        log.info("===============================================");
    }

    /**
     * SSE 事件记录
     */
    private record SseEvent(String event, String data, long elapsed) {}
}
