package dev.dong4j.zeka.starter.messaging.template;

import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.adapter.MessagingTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;

public class KafkaMessagingTemplateAdapter implements MessagingTemplateAdapter {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaMessagingTemplateAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public SendResult sendSync(UnifiedMessage message) {
        ProducerRecord<String, Object> record = createProducerRecord(message);
        CompletableFuture<org.springframework.kafka.support.SendResult<String, Object>> future =
            kafkaTemplate.send(record);

        try {
            org.springframework.kafka.support.SendResult<?, ?> result = future.get();
            return convertSendResult(result);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send Kafka message", e);
        }
    }

    @Override
    public CompletableFuture<SendResult> sendAsync(UnifiedMessage message) {
        ProducerRecord<String, Object> record = createProducerRecord(message);
        return kafkaTemplate.send(record)
            .thenApply(this::convertSendResult)
            .exceptionally(ex -> {
                throw new RuntimeException("Async send failed", ex);
            });
    }

    @Override
    public void sendOneWay(UnifiedMessage message) {
        ProducerRecord<String, Object> record = createProducerRecord(message);
        kafkaTemplate.send(record);
    }

    @Override
    public <T> T getNativeTemplate() {
        // noinspection unchecked
        return (T) kafkaTemplate;
    }

    private ProducerRecord<String, Object> createProducerRecord(UnifiedMessage message) {
        String topic = message.getDestination();
        Object payload = message.getPayload();

        if (message.getMessageKey() != null) {
            return new ProducerRecord<>(topic, message.getMessageKey().getKey().toString(), payload);
        }
        return new ProducerRecord<>(topic, payload);
    }

    private SendResult convertSendResult(org.springframework.kafka.support.SendResult<?, ?> result) {
        return new SendResult(
            result.getRecordMetadata().topic(),
            result.getRecordMetadata().partition(),
            result.getRecordMetadata().offset(),
            result.getProducerRecord().key().toString()
        );
    }
}
