// MessageContext.java
package dev.dong4j.zeka.starter.messaging.context;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MessagingContext {
    // Getters and setters
    private MessagingType messagingType;
    private String topic;
    private String groupId;
    private UnifiedMessage message;

    public MessagingContext() {
    }

    public MessagingContext(MessagingType messagingType) {
        this.messagingType = messagingType;
    }

}
