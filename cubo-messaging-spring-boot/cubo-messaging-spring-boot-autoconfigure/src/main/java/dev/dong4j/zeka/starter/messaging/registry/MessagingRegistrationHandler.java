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

/**
 * 消息注册处理器
 * <p>
 * 该类负责管理不同消息中间件的监听容器工厂，并提供统一的注册接口。
 * <p>
 * 核心功能：
 * 1. 维护消息类型与容器工厂的映射关系
 * 2. 提供适配器的统一注册入口
 * 3. 支持多种消息中间件(Kafka/RocketMQ)的容器注册
 * <p>
 * 使用场景：
 * 1. 消息监听适配器的注册
 * 2. 消息中间件容器的管理
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class MessagingRegistrationHandler {

    /**
     * 消息类型与容器工厂的映射表
     */
    private final Map<MessagingType, MessagingListenerContainerFactory> factories = new HashMap<>();

    /**
     * 构造方法
     *
     * @param kafkaRegistry Kafka 监听器端点注册表
     * @param rocketmqRegistry RocketMQ 容器注册器
     */
    public MessagingRegistrationHandler(
        KafkaListenerEndpointRegistry kafkaRegistry,
        RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketmqRegistry) {

        // 注册Kafka工厂
        factories.put(MessagingType.KAFKA, new KafkaContainerFactoryProxy(kafkaRegistry));

        // 注册RocketMQ工厂
        factories.put(MessagingType.ROCKETMQ, new RocketMQContainerFactoryProxy(rocketmqRegistry));
    }

    /**
     * 注册消息监听适配器
     *
     * @param adapter 消息监听适配器
     * @param annotation MessagingListener 注解实例
     * @throws IllegalArgumentException 如果消息类型不支持
     */
    public void registerAdapter(AbstractMessagingListenerAdapter adapter, MessagingListener annotation) {
        MessagingType messagingType = annotation.type();
        MessagingListenerContainerFactory factory = factories.get(messagingType);
        if (factory == null) {
            throw new IllegalArgumentException("No container factory for MQ type: " + messagingType);
        }
        factory.registerContainer(adapter, annotation);
    }
}
