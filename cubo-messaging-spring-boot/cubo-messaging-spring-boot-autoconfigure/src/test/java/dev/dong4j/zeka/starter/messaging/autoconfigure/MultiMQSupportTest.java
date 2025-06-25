package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.Matchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class MultiMQSupportTest {

    @Autowired
    private MessagingTemplate messageTemplate;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Test
    void shouldSupportMultipleMQTypes() {
        // 发送Kafka消息
        UnifiedMessage kafkaMessage = new UnifiedMessage("kafka-topic", "kafka-data");
        messageTemplate.forType(MessagingType.KAFKA).sendSync(kafkaMessage);

        // 验证Kafka发送
        verify(kafkaTemplate).send((ProducerRecord<String, Object>) any(ProducerRecord.class));

        // 发送RocketMQ消息
        UnifiedMessage rocketMessage = new UnifiedMessage("rocket-topic", "rocket-data");
        messageTemplate.forType(MessagingType.ROCKETMQ).sendSync(rocketMessage);

        // 验证RocketMQ发送
        verify(rocketMQTemplate).syncSend(anyString(), any(Message.class));

        // 发送默认类型消息
        UnifiedMessage defaultMessage = new UnifiedMessage("default-topic", "default-data");
        messageTemplate.sendSync(defaultMessage);

        // 默认类型应为Kafka
        verify(kafkaTemplate, times(2)).send((ProducerRecord<String, Object>) any(ProducerRecord.class));
    }
}
