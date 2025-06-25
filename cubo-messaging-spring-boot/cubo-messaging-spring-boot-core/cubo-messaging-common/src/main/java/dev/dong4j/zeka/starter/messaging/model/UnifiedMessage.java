package dev.dong4j.zeka.starter.messaging.model;

import dev.dong4j.zeka.starter.messaging.template.model.MessageKey;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
public class UnifiedMessage {
    // Getters and setters
    @Setter
    private String destination;
    private final Object payload;
    private MessageKey messageKey;
    private final Map<String, Object> headers = new HashMap<>();

    public UnifiedMessage(String destination, Object payload) {
        this.destination = destination;
        this.payload = payload;
    }

    public UnifiedMessage addHeader(String key, Object value) {
        this.headers.put(key, value);
        return this;
    }

    public UnifiedMessage withKey(MessageKey key) {
        this.messageKey = key;
        return this;
    }

}
