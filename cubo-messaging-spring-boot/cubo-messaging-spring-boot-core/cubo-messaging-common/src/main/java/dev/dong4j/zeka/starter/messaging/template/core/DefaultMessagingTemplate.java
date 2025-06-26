// DefaultUnifiedMessageTemplate.java
package dev.dong4j.zeka.starter.messaging.template.core;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import dev.dong4j.zeka.starter.messaging.template.adapter.MessagingTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultMessagingTemplate implements MessagingTemplate {
    private final ConcurrentMap<MessagingType, MessagingTemplateAdapter> adapters = new ConcurrentHashMap<>();

    public void registerAdapter(MessagingType type, MessagingTemplateAdapter adapter) {
        adapters.put(type, adapter);
    }

    @Override
    public SendResult sendSync(UnifiedMessage message) {
        MessagingTemplateAdapter adapter = selectAdapter();
        return adapter.sendSync(message);
    }

    @Override
    public CompletableFuture<SendResult> sendAsync(UnifiedMessage message) {
        MessagingTemplateAdapter adapter = selectAdapter();
        return adapter.sendAsync(message);
    }

    @Override
    public void sendOneWay(UnifiedMessage message) {
        MessagingTemplateAdapter adapter = selectAdapter();
        adapter.sendOneWay(message);
    }

    @Override
    public <T> T getNativeTemplate(MessagingType type, Class<T> templateClass) {
        MessagingTemplateAdapter adapter = adapters.get(type);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter for type: " + type);
        }
        return adapter.getNativeTemplate();
    }

    @Override
    public MessagingTemplate forType(MessagingType type) {
        return new MessagingTemplateWrapper(adapters.get(type));
    }

    private MessagingTemplateAdapter selectAdapter() {
        if (adapters.size() == 1) {
            return adapters.values().iterator().next();
        }
        throw new IllegalStateException("Multiple MQ adapters available. Use forType() to specify.");
    }

    private static class MessagingTemplateWrapper implements MessagingTemplate {
        private final MessagingTemplateAdapter adapter;

        MessagingTemplateWrapper(MessagingTemplateAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public SendResult sendSync(UnifiedMessage message) {
            return adapter.sendSync(message);
        }

        @Override
        public CompletableFuture<SendResult> sendAsync(UnifiedMessage message) {
            return adapter.sendAsync(message);
        }

        @Override
        public void sendOneWay(UnifiedMessage message) {
            adapter.sendOneWay(message);
        }

        @Override
        public <T> T getNativeTemplate(MessagingType type, Class<T> templateClass) {
            return adapter.getNativeTemplate();
        }

        @Override
        public MessagingTemplate forType(MessagingType type) {
            return this;
        }
    }
}
