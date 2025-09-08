/**
 * 抽象消息监听适配器，提供消息处理的基础实现
 * <p>
 * 该类是消息监听适配器的抽象基类，主要功能包括：
 * 1. 封装消息处理的核心流程
 * 2. 提供消息格式转换的抽象方法
 * 3. 维护消息处理上下文
 * <p>
 * 子类需要实现 createUnifiedMessage 方法，将特定消息中间件的原始消息
 * 转换为统一的 UnifiedMessage 格式。
 * <p>
 * 使用场景：
 * 1. Kafka 消息监听适配器
 * 2. RocketMQ 消息监听适配器
 * 3. 其他消息中间件的适配器实现
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2023.05.15
 * @since 1.0.0
 */
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

    /**
     * 构造方法
     *
     * @param handlerMethod 消息处理方法
     * @param context 消息处理上下文
     * @param method 目标业务方法
     */
    public AbstractMessagingListenerAdapter(MessagingHandlerMethod handlerMethod,
                                            MessagingContext context,
                                            Method method) {
        this.handlerMethod = handlerMethod;
        this.context = context;
        this.method = method;
    }

    /**
     * 处理消息
     *
     * @param rawMessage 原始消息对象
     */
    protected void handleMessage(Object rawMessage) {
        // 转换消息格式
        UnifiedMessage unifiedMessage = createUnifiedMessage(rawMessage);
        context.setMessage(unifiedMessage);

        // 使用 UnifiedMessageHandlerMethod 调用业务方法
        handlerMethod.invoke(context);
    }

    /**
     * 创建统一消息对象
     *
     * @param rawMessage 原始消息对象
     * @return 统一格式的消息对象
     */
    protected abstract UnifiedMessage createUnifiedMessage(Object rawMessage);
}
