// RocketMQListenerAdapter.java
package dev.dong4j.zeka.starter.messaging.adapter;

import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.support.MessagingHandlerMethod;
import java.lang.reflect.Method;

public class RocketMQMessagingListenerAdapter extends AbstractMessagingListenerAdapter
    implements org.apache.rocketmq.spring.core.RocketMQListener<String> {

    public RocketMQMessagingListenerAdapter(MessagingHandlerMethod handlerMethod,
                                            MessagingContext context,
                                            Method method) {
        super(handlerMethod, context, method);
    }

    @Override
    public void onMessage(String message) {
        handleMessage(message);
    }

    @Override
    protected UnifiedMessage createUnifiedMessage(Object rawMessage) {
        // 实际应用中可能需要更复杂的转换
        return new UnifiedMessage(getContext().getTopic(), rawMessage);
    }
}
