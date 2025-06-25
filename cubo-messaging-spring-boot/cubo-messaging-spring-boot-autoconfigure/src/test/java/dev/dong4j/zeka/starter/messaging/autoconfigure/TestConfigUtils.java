package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.common.util.JsonUtils;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.model.MessageKey;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.rocketmq.common.message.MessageExt;

public class TestConfigUtils {

    public static UnifiedMessage createTestMessage(String topic, Object payload) {
        return new UnifiedMessage(topic, payload)
            .withKey(MessageKey.of(UUID.randomUUID().toString()))
            .addHeader("test-header", "test-value");
    }

    public static ConsumerRecord<String, Object> createKafkaRecord(
        String topic, Object payload, String key) {

        return new ConsumerRecord<>(topic, 0, 0, key, payload);
    }

    public static MessageExt createRocketMQMessage(
        String topic, String tags, Object payload) {

        MessageExt message = new MessageExt();
        message.setTopic(topic);
        message.setTags(tags);
        message.setBody(JsonUtils.toJsonAsBytes(payload));
        return message;
    }
}
