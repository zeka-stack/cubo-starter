package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ErrorHandlingTest {

    @Autowired
    private MessagingTemplate messageTemplate;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @SpyBean
    private ErrorHandler errorHandler;

    @Test
    void shouldHandleSendFailure() {
        // 模拟发送失败
        when(kafkaTemplate.send((Message<?>) any(ProducerRecord.class)))
            .thenThrow(new KafkaException("Send failed"));

        UnifiedMessage message = new UnifiedMessage("error-topic", "test-data");

        // 验证异常抛出
        assertThrows(RuntimeException.class, () -> {
            messageTemplate.sendSync(message);
        });

        // 验证错误处理器调用
        verify(errorHandler).handleSendError((UnifiedMessage) any(UnifiedMessage.class), (Exception) any(RuntimeException.class));
    }
}

// 自定义错误处理器
@Component
class ErrorHandler {

    @Autowired
    private MessagingTemplate messageTemplate;

    public void handleSendError(UnifiedMessage message, Exception ex) {
        // 重试或记录错误
        System.err.println("Failed to send message: " + ex.getMessage());

        // 示例：重试发送
        try {
            messageTemplate.sendSync(message);
        } catch (Exception retryEx) {
            System.err.println("Retry failed: " + retryEx.getMessage());
        }
    }
}
