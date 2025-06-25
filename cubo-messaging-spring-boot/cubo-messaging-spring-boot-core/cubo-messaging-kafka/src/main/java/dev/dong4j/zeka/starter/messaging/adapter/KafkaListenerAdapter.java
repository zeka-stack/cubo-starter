// KafkaListenerAdapter.java
package dev.dong4j.zeka.starter.messaging.adapter;

import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.support.MessagingHandlerMethod;
import java.lang.reflect.Method;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaListenerAdapter extends AbstractListenerAdapter {

    public KafkaListenerAdapter(MessagingHandlerMethod handlerMethod,
                                MessagingContext context,
                                Method method) {
        super(handlerMethod, context, method);
    }

    public void onMessage(ConsumerRecord<String, String> record) {
        handleMessage(record);
    }

    @Override
    protected UnifiedMessage createUnifiedMessage(Object rawMessage) {
        ConsumerRecord<String, String> record = (ConsumerRecord<String, String>) rawMessage;
        return new UnifiedMessage(record.topic(), record.value());
    }
}
