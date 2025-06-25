package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("perf")
class MessageThroughputTest {

    @Autowired
    private MessagingTemplate messageTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void testKafkaMessageThroughput() {
        // 预热
        warmup(100);

        // 测试参数
        int messageCount = 10_000;
        int batchSize = 100;

        // 批量发送测试
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < messageCount; i += batchSize) {
            List<CompletableFuture<SendResult>> futures = new ArrayList<>();

            for (int j = 0; j < batchSize; j++) {
                UnifiedMessage message = new UnifiedMessage("perf-topic", "message-" + (i + j));
                futures.add(messageTemplate.sendAsync(message));
            }

            // 等待批次完成
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        double throughput = (double) messageCount / (duration / 1000.0);

        System.out.printf("Sent %d messages in %d ms. Throughput: %.2f msg/s%n",
            messageCount, duration, throughput);

        // 验证发送调用次数
        verify(kafkaTemplate, times(messageCount)).send((Message<?>) any(ProducerRecord.class));
    }

    private void warmup(int count) {
        for (int i = 0; i < count; i++) {
            UnifiedMessage message = new UnifiedMessage("warmup-topic", "warmup-" + i);
            messageTemplate.sendSync(message);
        }
    }

}
