// UnifiedMessageTemplate.java
package dev.dong4j.zeka.starter.messaging.template;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;

public interface MessagingTemplate {
    SendResult sendSync(UnifiedMessage message);

    CompletableFuture<SendResult> sendAsync(UnifiedMessage message);

    void sendOneWay(UnifiedMessage message);

    <T> T getNativeTemplate(MessagingType type, Class<T> templateClass);

    MessagingTemplate forType(MessagingType type);
}
