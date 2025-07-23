// UnifiedMQConfiguration.java
package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.starter.messaging.factory.DefaultRocketMQContainerRegistry;
import dev.dong4j.zeka.starter.messaging.factory.RocketMQContainerFactoryProxy;
import dev.dong4j.zeka.starter.messaging.registry.MessagingListenerRegistry;
import dev.dong4j.zeka.starter.messaging.registry.MessagingRegistrationHandler;
import dev.dong4j.zeka.starter.messaging.util.MessagingTypeDetector;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

/**
 * 消费者自动装配
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@Configuration
@ConditionalOnEnabled(value = MessagingProperties.PREFIX)
@EnableConfigurationProperties(MessagingProperties.class)
public class MessagingContainerConfiguration {

    /**
     * 类型检测器
     *
     * @param environment 环境
     * @return {@link MessagingTypeDetector }
     */
    @Bean
    public MessagingTypeDetector messagingTypeDetector(Environment environment) {
        return new MessagingTypeDetector(environment);
    }

    /**
     * 提供 KafkaListenerEndpointRegistry Bean（通常由 Spring Kafka 自动配置）
     *
     * @return {@link KafkaListenerEndpointRegistry }
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }

    /**
     * 提供 RocketMQ 容器注册器
     *
     * @return {@link RocketMQContainerFactoryProxy.RocketMQContainerRegistry }
     */
    @Bean
    public RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketMQContainerRegistry() {
        return new DefaultRocketMQContainerRegistry();
    }

    /**
     * 创建 MQRegistrationHandler Bean
     *
     * @param kafkaRegistry    卡夫卡注册表
     * @param rocketmqRegistry RocketMQ注册表
     * @return {@link MessagingRegistrationHandler }
     */
    @Bean
    public MessagingRegistrationHandler messagingRegistrationHandler(KafkaListenerEndpointRegistry kafkaRegistry,
                                                                     RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketmqRegistry) {

        return new MessagingRegistrationHandler(kafkaRegistry, rocketmqRegistry);
    }

    /**
     * 消息传递听众注册表
     *
     * @param registrationHandler 注册处理程序
     * @param typeDetector        类型检测器
     * @return {@link MessagingListenerRegistry }
     */
    @Bean
    public MessagingListenerRegistry messagingListenerRegistry(MessagingRegistrationHandler registrationHandler, MessagingTypeDetector typeDetector) {
        return new MessagingListenerRegistry(registrationHandler, typeDetector);
    }


}
