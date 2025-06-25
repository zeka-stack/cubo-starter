package dev.dong4j.zeka.starter.messaging.handler;


import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;

@FunctionalInterface
public interface MessagingMessageHandler {
    void handleMessage(UnifiedMessage message, MessagingContext context);
}
