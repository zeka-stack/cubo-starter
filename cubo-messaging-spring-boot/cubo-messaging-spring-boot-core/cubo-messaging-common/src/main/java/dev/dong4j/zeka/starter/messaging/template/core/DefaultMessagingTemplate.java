package dev.dong4j.zeka.starter.messaging.template.core;

import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.template.MessagingTemplate;
import dev.dong4j.zeka.starter.messaging.template.adapter.MessagingTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.model.SendResult;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 默认消息模板实现类
 * <p>
 * 该类实现了 MessagingTemplate 接口，提供：
 * 1. 多消息中间件的统一发送接口
 * 2. 适配器的注册管理
 * 3. 消息发送的代理转发
 * <p>
 * 核心功能：
 * 1. 根据消息类型自动选择适配器
 * 2. 支持同步/异步/单向发送
 * 3. 提供原生模板访问
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class DefaultMessagingTemplate implements MessagingTemplate {
    /**
     * 适配器映射表
     */
    private final ConcurrentMap<MessagingType, MessagingTemplateAdapter> adapters = new ConcurrentHashMap<>();

    /**
     * 注册消息模板适配器
     *
     * @param type 消息类型
     * @param adapter 消息模板适配器
     */
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

    /**
     * 选择适配器
     *
     * @return 消息模板适配器
     * @throws IllegalStateException 如果存在多个适配器且未指定类型
     */
    private MessagingTemplateAdapter selectAdapter() {
        if (adapters.size() == 1) {
            return adapters.values().iterator().next();
        }
        throw new IllegalStateException("Multiple MQ adapters available. Use forType() to specify.");
    }

    /**
         * 消息模板包装类
         */
        private record MessagingTemplateWrapper(MessagingTemplateAdapter adapter) implements MessagingTemplate {

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
