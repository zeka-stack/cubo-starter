// DefaultUnifiedMessageTemplate.java
package dev.dong4j.zeka.starter.messaging.template.core;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import dev.dong4j.zeka.starter.messaging.template.adapter.TemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DefaultMessagingTemplate implements MessagingTemplate {
    private final ConcurrentMap<MessagingType, TemplateAdapter> adapters = new ConcurrentHashMap<>();

    public void registerAdapter(MessagingType type, TemplateAdapter adapter) {
        adapters.put(type, adapter);
    }

    @Override
    public SendResult sendSync(UnifiedMessage message) {
        TemplateAdapter adapter = selectAdapter();
        return adapter.sendSync(message);
    }

    @Override
    public CompletableFuture<SendResult> sendAsync(UnifiedMessage message) {
        TemplateAdapter adapter = selectAdapter();
        return adapter.sendAsync(message);
    }

    @Override
    public void sendOneWay(UnifiedMessage message) {
        TemplateAdapter adapter = selectAdapter();
        adapter.sendOneWay(message);
    }

    @Override
    public <T> T getNativeTemplate(MessagingType type, Class<T> templateClass) {
        TemplateAdapter adapter = adapters.get(type);
        if (adapter == null) {
            throw new IllegalArgumentException("No adapter for type: " + type);
        }
        return adapter.getNativeTemplate();
    }

    @Override
    public MessagingTemplate forType(MessagingType type) {
        return new MessagingTemplateWrapper(adapters.get(type));
    }

    private TemplateAdapter selectAdapter() {
        if (adapters.size() == 1) {
            return adapters.values().iterator().next();
        }
        throw new IllegalStateException("Multiple MQ adapters available. Use forType() to specify.");
    }

    private static class MessagingTemplateWrapper implements MessagingTemplate {
        private final TemplateAdapter adapter;

        MessagingTemplateWrapper(TemplateAdapter adapter) {
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
