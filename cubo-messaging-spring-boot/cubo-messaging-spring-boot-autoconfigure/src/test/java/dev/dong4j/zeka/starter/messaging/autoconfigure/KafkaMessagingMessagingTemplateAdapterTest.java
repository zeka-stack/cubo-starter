package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.KafkaMessagingTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.model.MessageKey;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class KafkaMessagingMessagingTemplateAdapterTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private KafkaMessagingTemplateAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new KafkaMessagingTemplateAdapter(kafkaTemplate);
    }

    @Test
    void shouldSendSyncMessage() throws Exception {
        // 准备测试消息
        UnifiedMessage message = new UnifiedMessage("test-topic", "payload")
            .withKey(MessageKey.of("key123"));

        // 执行测试
        SendResult result = adapter.sendSync(message);

        // 验证结果
        assertNotNull(result);
        assertEquals("test-topic", result.topic());
        assertEquals(0, result.partition());
    }

    @Test
    void shouldHandleAsyncSend() {
        // 准备测试消息
        UnifiedMessage message = new UnifiedMessage("async-topic", "async-payload");

        // 执行测试
        CompletableFuture<SendResult> asyncResult = adapter.sendAsync(message);

        // 验证结果
        SendResult result = asyncResult.join();
        assertEquals("async-topic", result.topic());
        assertEquals(1, result.partition());
    }
}
