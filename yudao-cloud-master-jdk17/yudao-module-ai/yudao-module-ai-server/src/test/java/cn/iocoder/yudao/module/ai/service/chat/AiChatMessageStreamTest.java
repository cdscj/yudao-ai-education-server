package cn.iocoder.yudao.module.ai.service.chat;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SSE 流式返回原理验证测试（纯 Java，无 Spring/DB 依赖）
 *
 * <p>直接测试 Reactor Flux 的流式特性，证明：
 * <ol>
 *   <li>Flux.delayElements 逐个发射元素（非批量）</li>
 *   <li>每个 chunk 到达时间戳不同</li>
 *   <li>纯文本 Flux 对比 包裹 CommonResult 的 Flux 的区别</li>
 * </ol>
 * </p>
 */
@Slf4j
public class AiChatMessageStreamTest {

    /**
     * 核心验证：证明 Reactor Flux 是真正的流式推送
     *
     * <p>模拟 6 个 chunk，每个间隔 100ms 发射。
     * 验证每个 chunk 到达的时间戳确实不同。</p>
     */
    @Test
    public void testFluxIsRealStreaming() throws InterruptedException {
        List<String> chunks = List.of(
                "{\"sendId\":1,\"receiveId\":2}",   // metadata
                "你", "好", "世", "界",               // content
                "[DONE]"                              // end
        );

        List<Long> timestamps = new ArrayList<>();
        List<String> arrivedChunks = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(chunks.size());

        // 模拟 SSE flux
        Flux.fromIterable(chunks)
                .delayElements(Duration.ofMillis(100))
                .doOnNext(chunk -> {
                    timestamps.add(System.currentTimeMillis());
                    arrivedChunks.add(chunk);
                    latch.countDown();
                })
                .subscribe();

        boolean completed = latch.await(5, TimeUnit.SECONDS);
        assertTrue(completed);

        // === 断言 1：逐个到达，时间戳递增 ===
        assertEquals(6, arrivedChunks.size());
        assertEquals(6, timestamps.size());

        long totalGap = 0;
        for (int i = 1; i < timestamps.size(); i++) {
            long gap = timestamps.get(i) - timestamps.get(i - 1);
            totalGap += gap;
            log.info("[chunk {}] 内容='{}', 距上一个={}ms, 总计={}ms",
                    i, arrivedChunks.get(i), gap, timestamps.get(i) - timestamps.get(0));
        }

        long totalDuration = timestamps.get(timestamps.size() - 1) - timestamps.get(0);
        log.info("✅ 6个chunk逐个到达，总耗时={}ms, chunk间间隔累计={}ms", totalDuration, totalGap);

        // 至少所有的chunk不是同时到达的
        assertTrue(totalDuration > 100,
                "总耗时仅 " + totalDuration + "ms，所有chunk同时到达，不是流式！");

        // === 断言 2：首事件是 metadata JSON ===
        assertTrue(arrivedChunks.get(0).startsWith("{"));
        assertTrue(arrivedChunks.get(0).contains("sendId"));

        // === 断言 3：末事件是 [DONE] ===
        assertEquals("[DONE]", arrivedChunks.get(arrivedChunks.size() - 1));

        log.info("流式验证全部通过 ✅");
    }

    /**
     * 对比测试：新方案（纯文本 Flux<String>）vs 旧方案（Flux<CommonResult<T>>）
     *
     * <p>旧方案每个 chunk 包裹在 CommonResult 里，数据量膨胀。
     * 新方案每个 chunk 只传输纯文本。
     * </p>
     */
    @Test
    public void testCompareOldVsNewPayloadSize() {
        // 旧方案：CommonResult 包裹
        String oldChunk = "{\"code\":0,\"data\":{\"send\":{\"id\":1,\"model\":\"gpt-4\"},\"receive\":{\"id\":2,\"content\":\"你\",\"segments\":null,\"webSearchPages\":null}},\"msg\":\"\"}";
        String actualContent = "你";

        // 新方案：纯文本
        String newChunk = "你";

        int oldSize = oldChunk.getBytes().length;
        int newSize = newChunk.getBytes().length;

        log.info("=== 单 chunk 大小对比 ===");
        log.info("旧方案 (CommonResult包裹): {} bytes", oldSize);
        log.info("新方案 (纯文本):           {} bytes", newSize);
        log.info("膨胀比:                    {}x", oldSize / newSize);

        assertTrue(oldSize > newSize * 10,
                "旧方案应该比新方案大很多倍！实际: " + oldSize + " vs " + newSize);

        // 模拟 50 个 chunk 的对话
        int chunkCount = 50;
        log.info("=== 50 chunk 对话流量对比 ===");
        log.info("旧方案: {} bytes ≈ {} KB", oldSize * chunkCount, oldSize * chunkCount / 1024);
        log.info("新方案: {} bytes ≈ {} KB", newSize * chunkCount, newSize * chunkCount / 1024);
        log.info("节省:   {} bytes ≈ {} KB ({}x)",
                (oldSize - newSize) * chunkCount,
                (oldSize - newSize) * chunkCount / 1024,
                oldSize / newSize);

        log.info("payload对比验证通过 ✅");
    }
}
