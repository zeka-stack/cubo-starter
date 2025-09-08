// UnifiedMQConfiguration.java
package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.starter.messaging.factory.DefaultRocketMQContainerRegistry;
import dev.dong4j.zeka.starter.messaging.factory.RocketMQContainerFactoryProxy;
import dev.dong4j.zeka.starter.messaging.registry.MessagingListenerRegistry;
import dev.dong4j.zeka.starter.messaging.registry.MessagingRegistrationHandler;
import dev.dong4j.zeka.starter.messaging.util.MessagingTypeDetector;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

/**
 * 消息容器自动配置类
 *
 * 该类负责配置消息监听容器相关的组件，包括：
 * 1. 消息类型检测器
 * 2. Kafka 监听器端点注册表
 * 3. RocketMQ 容器注册器
 * 4. 消息注册处理器
 * 5. 消息监听器注册表
 *
 * 使用场景：
 * 1. 自动配置消息监听容器
 * 2. 集成多种消息中间件(Kafka, RocketMQ)
 * 3. 提供统一的注册管理机制
 *
 * 配置属性前缀：zeka.messaging
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnEnabled(value = MessagingProperties.PREFIX)
@EnableConfigurationProperties(MessagingProperties.class)
public class MessagingContainerConfiguration {

    /**
     * 创建消息类型检测器 Bean
     *
     * @param environment Spring 环境对象
     * @return 消息类型检测器实例
     */
    @Bean
    public MessagingTypeDetector messagingTypeDetector(Environment environment) {
        return new MessagingTypeDetector(environment);
    }

    /**
     * 创建 Kafka 监听器端点注册表 Bean
     *
     * 注意：如果 Spring Kafka 已经自动配置了该 Bean，则不会重复创建
     *
     * @return Kafka 监听器端点注册表实例
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
        return new KafkaListenerEndpointRegistry();
    }

    /**
     * 创建 RocketMQ 容器注册器 Bean
     *
     * @return RocketMQ 容器注册器实例
     */
    @Bean
    public RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketMQContainerRegistry() {
        return new DefaultRocketMQContainerRegistry();
    }

    /**
     * 创建消息注册处理器 Bean
     *
     * @param kafkaRegistry    Kafka 监听器端点注册表
     * @param rocketmqRegistry RocketMQ 容器注册器
     * @return 消息注册处理器实例
     */
    @Bean
    public MessagingRegistrationHandler messagingRegistrationHandler(KafkaListenerEndpointRegistry kafkaRegistry,
                                                                     RocketMQContainerFactoryProxy.RocketMQContainerRegistry rocketmqRegistry) {

        return new MessagingRegistrationHandler(kafkaRegistry, rocketmqRegistry);
    }

    /**
     * 创建消息监听器注册表 Bean
     *
     * @param registrationHandler 消息注册处理器
     * @param typeDetector        消息类型检测器
     * @return 消息监听器注册表实例
     */
    @Bean
    public MessagingListenerRegistry messagingListenerRegistry(MessagingRegistrationHandler registrationHandler, MessagingTypeDetector typeDetector) {
        return new MessagingListenerRegistry(registrationHandler, typeDetector);
    }


}
