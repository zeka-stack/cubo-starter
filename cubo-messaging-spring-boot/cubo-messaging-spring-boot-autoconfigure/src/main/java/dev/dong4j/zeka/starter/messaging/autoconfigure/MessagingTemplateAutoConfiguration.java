// UnifiedTemplateAutoConfiguration.java
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

@AutoConfiguration
@ConditionalOnEnabled(value = MessagingProperties.PREFIX)
public class MessagingTemplateAutoConfiguration {

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    public MessagingTemplateAdapter kafkaTemplateAdapter(KafkaTemplate<?, ?> kafkaTemplate) {
        // noinspection unchecked
        return new KafkaMessagingTemplateAdapter((KafkaTemplate<String, Object>) kafkaTemplate);
    }

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    public MessagingTemplateAdapter rocketMQTemplateAdapter(RocketMQTemplate rocketMQTemplate) {
        return new RocketMQMessagingTemplateAdapter(rocketMQTemplate);
    }

    @Bean
    public DefaultMessagingTemplate unifiedMessageTemplate(List<MessagingTemplateAdapter> availableAdapters) {

        DefaultMessagingTemplate template = new DefaultMessagingTemplate();

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
