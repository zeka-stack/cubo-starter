// UnifiedTemplateAutoConfiguration.java
package dev.dong4j.zeka.starter.messaging.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.starter.messaging.enums.MessagingType;
import dev.dong4j.zeka.starter.messaging.template.KafkaTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.RocketMQTemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.adapter.TemplateAdapter;
import dev.dong4j.zeka.starter.messaging.template.core.DefaultMessagingTemplate;
import java.util.List;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@ConditionalOnProperty(
    prefix = MessagingProperties.PREFIX,
    name = ZekaProperties.ENABLE,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true
)
public class MessagingTemplateAutoConfiguration {

    @Bean
    @ConditionalOnBean(KafkaTemplate.class)
    public TemplateAdapter kafkaTemplateAdapter(KafkaTemplate<?, ?> kafkaTemplate) {
        // noinspection unchecked
        return new KafkaTemplateAdapter((KafkaTemplate<String, Object>) kafkaTemplate);
    }

    @Bean
    @ConditionalOnBean(RocketMQTemplate.class)
    public TemplateAdapter rocketMQTemplateAdapter(RocketMQTemplate rocketMQTemplate) {
        return new RocketMQTemplateAdapter(rocketMQTemplate);
    }

    @Bean
    public DefaultMessagingTemplate unifiedMessageTemplate(List<TemplateAdapter> availableAdapters) {

        DefaultMessagingTemplate template = new DefaultMessagingTemplate();

        for (TemplateAdapter adapter : availableAdapters) {
            if (adapter instanceof KafkaTemplateAdapter) {
                template.registerAdapter(MessagingType.KAFKA, adapter);
            } else if (adapter instanceof RocketMQTemplateAdapter) {
                template.registerAdapter(MessagingType.ROCKETMQ, adapter);
            }
        }

        return template;
    }
}
