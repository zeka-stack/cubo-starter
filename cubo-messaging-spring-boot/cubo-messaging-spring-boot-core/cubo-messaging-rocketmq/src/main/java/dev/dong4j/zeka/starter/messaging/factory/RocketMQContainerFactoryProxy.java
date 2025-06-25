package dev.dong4j.zeka.starter.messaging.factory;

import dev.dong4j.zeka.starter.messaging.adapter.AbstractListenerAdapter;
import dev.dong4j.zeka.starter.messaging.adapter.RocketMQListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;

public class RocketMQContainerFactoryProxy implements MessagingListenerContainerFactory {

    public interface RocketMQContainerRegistry {
        void registerContainer(DefaultRocketMQListenerContainer container);
    }

    private final RocketMQContainerRegistry registry;

    public RocketMQContainerFactoryProxy(RocketMQContainerRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void registerContainer(AbstractListenerAdapter adapter, MessagingListener annotation) {
        if (adapter instanceof RocketMQListenerAdapter) {
            DefaultRocketMQListenerContainer container = createRocketMQListenerContainer((RocketMQListener) adapter, annotation);
            registry.registerContainer(container);
        } else {
            throw new IllegalArgumentException("Adapter must be instance of RocketMQListenerAdapter");
        }
    }

    private DefaultRocketMQListenerContainer createRocketMQListenerContainer(RocketMQListener listener, MessagingListener annotation) {
        DefaultRocketMQListenerContainer container = new DefaultRocketMQListenerContainer();
        container.setNameServer("localhost:9876"); // 实际应用中应从配置获取
        container.setTopic(annotation.topic());
        container.setConsumerGroup(annotation.groupId());
        container.setRocketMQListener(listener);
        return container;
    }
}
