package dev.dong4j.zeka.starter.messaging.registry;

import dev.dong4j.zeka.starter.messaging.adapter.AbstractMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.factory.KafkaContainerFactoryProxy;
import dev.dong4j.zeka.starter.messaging.factory.MessagingListenerContainerFactory;
import dev.dong4j.zeka.starter.messaging.factory.RocketMQContainerFactoryProxy;
import java.util.HashMap;
import java.util.Map;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

public class MessagingRegistrationHandler {

    private final Map<MessagingType, MessagingListenerContainerFactory> factories = new HashMap<>();

    public MessagingRegistrationHandler(
        KafkaListenerEndpointRegistry kafkaRegistry,
        RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketmqRegistry) {

        // 注册Kafka工厂
        factories.put(MessagingType.KAFKA, new KafkaContainerFactoryProxy(kafkaRegistry));

        // 注册RocketMQ工厂
        factories.put(MessagingType.ROCKETMQ, new RocketMQContainerFactoryProxy(rocketmqRegistry));
    }

    public void registerAdapter(AbstractMessagingListenerAdapter adapter, MessagingListener annotation) {
        MessagingType messagingType = annotation.type();
        MessagingListenerContainerFactory factory = factories.get(messagingType);
        if (factory == null) {
            throw new IllegalArgumentException("No container factory for MQ type: " + messagingType);
        }
        factory.registerContainer(adapter, annotation);
    }
}
