package dev.dong4j.zeka.starter.messaging.factory;

import dev.dong4j.zeka.starter.messaging.adapter.AbstractMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.adapter.KafkaMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import dev.dong4j.zeka.starter.messaging.util.MethodInvokerWrapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

public class KafkaContainerFactoryProxy implements MessagingListenerContainerFactory {

    private final KafkaListenerEndpointRegistry registry;

    public KafkaContainerFactoryProxy(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void registerContainer(AbstractMessagingListenerAdapter adapter, MessagingListener annotation) {
        if (adapter instanceof KafkaMessagingListenerAdapter) {
            KafkaListenerEndpoint endpoint = createKafkaListenerEndpoint(adapter, annotation);
            registry.registerListenerContainer(endpoint, null, true);
        } else {
            throw new IllegalArgumentException("Adapter must be instance of KafkaListenerAdapter");
        }
    }

    private KafkaListenerEndpoint createKafkaListenerEndpoint(AbstractMessagingListenerAdapter adapter, MessagingListener annotation) {
        MethodKafkaListenerEndpoint<String, String> endpoint = new MethodKafkaListenerEndpoint<>();
        endpoint.setId(annotation.groupId());
        endpoint.setGroupId(annotation.groupId());
        endpoint.setTopics(annotation.topic());
        endpoint.setBean(createMethodInvokerProxy(adapter));

        // 获取监听器方法
        try {
            endpoint.setMethod(adapter.getClass().getMethod("onMessage", ConsumerRecord.class));
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to find onMessage method", e);
        }

        DefaultMessageHandlerMethodFactory methodFactory = new DefaultMessageHandlerMethodFactory();
        methodFactory.afterPropertiesSet();
        endpoint.setMessageHandlerMethodFactory(methodFactory);

        return endpoint;
    }

    private Object createMethodInvokerProxy(AbstractMessagingListenerAdapter adapter) {
        MethodInvokerWrapper invoker = new MethodInvokerWrapper(
            adapter,
            adapter.getClass().getMethods()[0],
            args -> {
                // 调用适配器的 onMessage 方法
                if (args.length > 0 && args[0] instanceof ConsumerRecord) {
                    ((KafkaMessagingListenerAdapter) adapter).onMessage((ConsumerRecord) args[0]);
                }
                return null;
            }
        );
        return invoker.getProxy();
    }
}
