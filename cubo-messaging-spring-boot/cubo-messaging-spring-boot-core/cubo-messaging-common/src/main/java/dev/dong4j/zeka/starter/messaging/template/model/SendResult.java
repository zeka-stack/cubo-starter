package dev.dong4j.zeka.starter.messaging.template.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendResult {
    // Getters
    private final String topic;
    private final int partition;
    private final long offset;
    private final String messageId;

    public SendResult(String topic, int partition, long offset, String messageId) {
        this.topic = topic;
        this.partition = partition;
        this.offset = offset;
        this.messageId = messageId;
    }

    @Override
    public String toString() {
        return "SendResult{" +
            "topic='" + topic + '\'' +
            ", partition=" + partition +
            ", offset=" + offset +
            ", messageId='" + messageId + '\'' +
            '}';
    }
}
