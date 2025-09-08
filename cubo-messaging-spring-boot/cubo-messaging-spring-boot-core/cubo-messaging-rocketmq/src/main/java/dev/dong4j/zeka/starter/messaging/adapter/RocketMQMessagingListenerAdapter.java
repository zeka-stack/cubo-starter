package dev.dong4j.zeka.starter.messaging.adapter;

import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.support.MessagingHandlerMethod;
import java.lang.reflect.Method;

/**
 * RocketMQ 消息监听适配器
 * <p>
 * 该类继承自 AbstractMessagingListenerAdapter 并实现 RocketMQListener 接口，
 * 用于将 RocketMQ 消息转换为统一消息格式进行处理。
 * <p>
 * 主要功能：
 * 1. 实现 RocketMQ 消息监听接口
 * 2. 将 RocketMQ 原生消息转换为统一消息格式
 * 3. 代理消息处理方法调用
 * <p>
 * 使用场景：
 * 1. RocketMQ 消息监听器的实现
 * 2. 统一消息框架与 RocketMQ 的集成
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class RocketMQMessagingListenerAdapter extends AbstractMessagingListenerAdapter
    implements org.apache.rocketmq.spring.core.RocketMQListener<String> {

    /**
     * 构造方法
     *
     * @param handlerMethod 消息处理方法封装
     * @param context       消息上下文
     * @param method        目标方法
     */
    public RocketMQMessagingListenerAdapter(MessagingHandlerMethod handlerMethod,
                                            MessagingContext context,
                                            Method method) {
        super(handlerMethod, context, method);
    }

    /**
     * 处理 RocketMQ 消息
     *
     * @param message RocketMQ 原始消息
     */
    @Override
    public void onMessage(String message) {
        handleMessage(message);
    }

    /**
     * 创建统一消息对象
     *
     * @param rawMessage 原始消息对象
     * @return 统一消息对象
     */
    @Override
    protected UnifiedMessage createUnifiedMessage(Object rawMessage) {
        // 实际应用中可能需要更复杂的转换
        return new UnifiedMessage(getContext().getTopic(), rawMessage);
    }
}
