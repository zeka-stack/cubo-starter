package dev.dong4j.zeka.starter.messaging.factory;

import dev.dong4j.zeka.starter.messaging.adapter.AbstractMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.adapter.RocketMQMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;

/**
 * RocketMQ 容器工厂代理类
 * <p>
 * 该类实现了 MessagingListenerContainerFactory 接口，用于：
 * 1. 创建 RocketMQ 监听容器
 * 2. 注册 RocketMQ 监听器
 * 3. 管理 RocketMQ 监听容器的生命周期
 * <p>
 * 核心功能：
 * 1. 将统一消息监听适配器转换为 RocketMQ 监听容器
 * 2. 配置 RocketMQ 监听容器的基本参数
 * 3. 提供容器注册接口
 * <p>
 * 使用场景：
 * 1. RocketMQ 消息监听器的注册
 * 2. 统一消息框架与 RocketMQ 的集成
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class RocketMQContainerFactoryProxy implements MessagingListenerContainerFactory {

    /**
     * RocketMQ 容器注册接口
     */
    public interface RocketMQContainerRegistry {
        /**
         * 注册 RocketMQ 监听容器
         *
         * @param container RocketMQ 监听容器
         */
        void registerContainer(DefaultRocketMQListenerContainer container);
    }

    /**
     * RocketMQ 容器注册表
     */
    private final RocketMQContainerRegistry registry;

    /**
     * 构造方法
     *
     * @param registry RocketMQ 容器注册表
     */
    public RocketMQContainerFactoryProxy(RocketMQContainerRegistry registry) {
        this.registry = registry;
    }

    /**
     * 注册 RocketMQ 监听容器
     *
     * @param adapter    消息监听适配器
     * @param annotation MessagingListener 注解实例
     * @throws IllegalArgumentException 如果适配器类型不匹配
     */
    @Override
    public void registerContainer(AbstractMessagingListenerAdapter adapter, MessagingListener annotation) {
        if (adapter instanceof RocketMQMessagingListenerAdapter) {
            DefaultRocketMQListenerContainer container = createRocketMQListenerContainer((RocketMQListener) adapter, annotation);
            registry.registerContainer(container);
        } else {
            throw new IllegalArgumentException("Adapter must be instance of RocketMQListenerAdapter");
        }
    }

    /**
     * 创建 RocketMQ 监听容器
     *
     * @param listener RocketMQ 监听器
     * @param annotation MessagingListener 注解实例
     * @return RocketMQ 监听容器
     */
    private DefaultRocketMQListenerContainer createRocketMQListenerContainer(RocketMQListener listener, MessagingListener annotation) {
        DefaultRocketMQListenerContainer container = new DefaultRocketMQListenerContainer();
        container.setNameServer("localhost:9876"); // 实际应用中应从配置获取
        container.setTopic(annotation.topic());
        container.setConsumerGroup(annotation.groupId());
        container.setRocketMQListener(listener);
        return container;
    }
}
