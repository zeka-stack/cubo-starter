package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.template.KafkaMessagingTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.RocketMQMessagingTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.adapter.MessagingTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.core.DefaultMessagingTemplate;
import java.util.List;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 消息模板自动配置类
 * <p>
 * 该类负责配置消息发送模板相关的组件，包括：
 * 1. Kafka 消息模板适配器
 * 2. RocketMQ 消息模板适配器
 * 3. 默认消息模板
 * <p>
 * 配置属性前缀：zeka.messaging
 * <p>
 * 使用场景：
 * 1. 自动配置消息发送模板
 * 2. 集成多种消息中间件(Kafka, RocketMQ)的发送能力
 * 3. 提供统一的消息发送接口
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
@AutoConfiguration
@ConditionalOnEnabled(value = MessagingProperties.PREFIX)
public class MessagingTemplateAutoConfiguration {

    /**
     * 创建 Kafka 消息模板适配器
     *
     * @param kafkaTemplate Spring Kafka 模板
     * @return Kafka 消息模板适配器实例
     */
    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    public MessagingTemplateAdapter kafkaTemplateAdapter(KafkaTemplate<?, ?> kafkaTemplate) {
        // noinspection unchecked
        return new KafkaMessagingTemplateAdapter((KafkaTemplate<String, Object>) kafkaTemplate);
    }

    /**
     * 创建 RocketMQ 消息模板适配器
     *
     * @param rocketMQTemplate RocketMQ 模板
     * @return RocketMQ 消息模板适配器实例
     */
    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    public MessagingTemplateAdapter rocketMQTemplateAdapter(RocketMQTemplate rocketMQTemplate) {
        return new RocketMQMessagingTemplateAdapter(rocketMQTemplate);
    }

    /**
     * 创建默认消息模板
     *
     * @param availableAdapters 可用的消息模板适配器列表
     * @return 默认消息模板实例
     */
    @Bean
    public DefaultMessagingTemplate unifiedMessageTemplate(List<MessagingTemplateAdapter> availableAdapters) {
        DefaultMessagingTemplate template = new DefaultMessagingTemplate();

        // 注册所有可用的适配器
        for (MessagingTemplateAdapter adapter : availableAdapters) {
            if (adapter instanceof KafkaMessagingTemplateAdapter) {
                template.registerAdapter(MessagingType.KAFKA, adapter);
            } else if (adapter instanceof RocketMQMessagingTemplateAdapter) {
                template.registerAdapter(MessagingType.ROCKETMQ, adapter);
            }
        }

        return template;
    }
}
