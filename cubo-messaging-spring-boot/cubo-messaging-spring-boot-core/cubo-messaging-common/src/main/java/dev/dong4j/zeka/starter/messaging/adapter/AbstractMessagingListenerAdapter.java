// AbstractListenerAdapter.java
package dev.dong4j.zeka.starter.messaging.adapter;

import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.support.MessagingHandlerMethod;
import java.lang.reflect.Method;
import lombok.Getter;

public abstract class AbstractMessagingListenerAdapter {
    private final MessagingHandlerMethod handlerMethod;
    @Getter
    private final MessagingContext context;
    private final Method method;

    public AbstractMessagingListenerAdapter(MessagingHandlerMethod handlerMethod,
                                            MessagingContext context,
                                            Method method) {
        this.handlerMethod = handlerMethod;
        this.context = context;
        this.method = method;
    }

    protected void handleMessage(Object rawMessage) {
        // 转换消息格式
        UnifiedMessage unifiedMessage = createUnifiedMessage(rawMessage);
        context.setMessage(unifiedMessage);

        // 使用 UnifiedMessageHandlerMethod 调用业务方法
        handlerMethod.invoke(context);
    }

    protected abstract UnifiedMessage createUnifiedMessage(Object rawMessage);
}
