// UnifiedMQConfiguration.java
package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.starter.messaging.factory.DefaultRocketMQContainerRegistry;
import dev.dong4j.zeka.starter.messaging.factory.RocketMQContainerFactoryProxy;
import dev.dong4j.zeka.starter.messaging.registry.MessagingListenerRegistry;
import dev.dong4j.zeka.starter.messaging.registry.MessagingRegistrationHandler;
import dev.dong4j.zeka.starter.messaging.util.MessagingTypeDetector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

@Configuration
@ConditionalOnProperty(
    prefix = MessagingProperties.PREFIX,
    name = ZekaProperties.ENABLE,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true
)
@EnableConfigurationProperties(MessagingProperties.class)
public class MessagingConfiguration {

    @Bean
    public MessagingTypeDetector mqTypeDetector(Environment environment) {
        return new MessagingTypeDetector(environment);
    }

    // 提供 KafkaListenerEndpointRegistry Bean（通常由 Spring Kafka 自动配置）
    @Bean
    @ConditionalOnMissingBean
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }

    // 提供 RocketMQ 容器注册器
    @Bean
    public RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketMQContainerRegistry() {
        return new DefaultRocketMQContainerRegistry();
    }

    // 创建 MQRegistrationHandler Bean
    @Bean
    public MessagingRegistrationHandler mqRegistrationHandler(KafkaListenerEndpointRegistry kafkaRegistry,
                                                              RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketmqRegistry) {

        return new MessagingRegistrationHandler(kafkaRegistry, rocketmqRegistry);
    }

    @Bean
    public MessagingListenerRegistry unifiedListenerRegistry(MessagingRegistrationHandler registrationHandler, MessagingTypeDetector typeDetector) {
        return new MessagingListenerRegistry(registrationHandler, typeDetector);
    }


}
