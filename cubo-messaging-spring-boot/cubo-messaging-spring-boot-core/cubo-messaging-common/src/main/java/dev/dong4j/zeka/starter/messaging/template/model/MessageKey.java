package dev.dong4j.zeka.starter.messaging.template.model;

public class MessageKey {
    private final Object key;

    private MessageKey(Object key) {
        this.key = key;
    }

    public static MessageKey of(Object key) {
        return new MessageKey(key);
    }

    public Object getKey() {
        return key;
    }
}
