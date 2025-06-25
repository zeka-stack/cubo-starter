package dev.dong4j.zeka.starter.messaging.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PreDestroy;
import org.apache.rocketmq.spring.support.DefaultRocketMQListenerContainer;

public class DefaultRocketMQContainerRegistry implements RocketMQContainerFactoryProxy.RocketMQContainerRegistry {

    private final Map<String, DefaultRocketMQListenerContainer> containers = new ConcurrentHashMap<>();

    @Override
    public void registerContainer(DefaultRocketMQListenerContainer container) {
        String key = container.getConsumerGroup() + "-" + container.getTopic();
        containers.put(key, container);

        // 启动容器
        if (!container.isRunning()) {
            container.start();
        }
    }

    public void destroy() {
        containers.values().forEach(DefaultRocketMQListenerContainer::destroy);
    }

    @PreDestroy
    public void onDestroy() {
        destroy();
    }
}
