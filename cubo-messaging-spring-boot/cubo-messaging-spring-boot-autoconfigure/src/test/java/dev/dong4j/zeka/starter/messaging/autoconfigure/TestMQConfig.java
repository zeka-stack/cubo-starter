package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.starter.messaging.factory.RocketMQContainerFactoryProxy;
import dev.dong4j.zeka.starter.messaging.registry.MessagingListenerRegistry;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.mock;

@Configuration
@Profile("test")
public class TestMQConfig {

    // 模拟 Kafka 模板
    @Bean
    @Primary
    public KafkaTemplate<String, Object> testKafkaTemplate() {
        return mock(KafkaTemplate.class);
    }

    // 模拟 RocketMQ 模板
    @Bean
    @Primary
    public RocketMQTemplate testRocketMQTemplate() {
        return mock(RocketMQTemplate.class);
    }

    // 模拟 Kafka 监听器注册表
    @Bean
    @Primary
    public KafkaListenerEndpointRegistry testKafkaRegistry() {
        return mock(KafkaListenerEndpointRegistry.class);
    }

    // 模拟 RocketMQ 容器注册表
    @Bean
    @Primary
    public RocketMQContainerFactoryProxy.RocketMQContainerRegistry testRocketMQRegistry() {
        return mock(RocketMQContainerFactoryProxy.RocketMQContainerRegistry.class);
    }

    // 禁用真实的消息监听器注册
    @Bean
    @Primary
    public MessagingListenerRegistry testUnifiedListenerRegistry() {
        return mock(MessagingListenerRegistry.class);
    }
}
