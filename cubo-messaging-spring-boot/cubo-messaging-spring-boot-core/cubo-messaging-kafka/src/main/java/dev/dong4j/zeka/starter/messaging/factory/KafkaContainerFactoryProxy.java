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

/**
 * Kafka 容器工厂代理类
 * <p>
 * 该类实现了 MessagingListenerContainerFactory 接口，用于：
 * 1. 创建 Kafka 监听端点
 * 2. 注册 Kafka 监听容器
 * 3. 代理 Kafka 消息监听器的调用
 * <p>
 * 核心功能：
 * 1. 将统一消息监听适配器转换为 Kafka 监听端点
 * 2. 管理 Kafka 监听容器的生命周期
 * 3. 提供方法调用代理
 * <p>
 * 使用场景：
 * 1. Kafka 消息监听器的注册
 * 2. 统一消息框架与 Spring Kafka 的集成
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class KafkaContainerFactoryProxy implements MessagingListenerContainerFactory {

    /**
     * Kafka 监听端点注册表
     */
    private final KafkaListenerEndpointRegistry registry;

    /**
     * 构造方法
     *
     * @param registry Kafka 监听端点注册表
     */
    public KafkaContainerFactoryProxy(KafkaListenerEndpointRegistry registry) {
        this.registry = registry;
    }

    /**
     * 注册 Kafka 监听容器
     *
     * @param adapter    消息监听适配器
     * @param annotation MessagingListener 注解实例
     * @throws IllegalArgumentException 如果适配器类型不匹配
     */
    @Override
    public void registerContainer(AbstractMessagingListenerAdapter adapter, MessagingListener annotation) {
        if (adapter instanceof KafkaMessagingListenerAdapter) {
            KafkaListenerEndpoint endpoint = createKafkaListenerEndpoint(adapter, annotation);
            registry.registerListenerContainer(endpoint, null, true);
        } else {
            throw new IllegalArgumentException("Adapter must be instance of KafkaListenerAdapter");
        }
    }

    /**
     * 创建 Kafka 监听端点
     *
     * @param adapter 消息监听适配器
     * @param annotation MessagingListener 注解实例
     * @return Kafka 监听端点
     */
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

    /**
     * 创建方法调用代理
     *
     * @param adapter 消息监听适配器
     * @return 代理对象
     */
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
