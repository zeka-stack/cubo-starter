package dev.dong4j.zeka.starter.messaging.adapter;

import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import dev.dong4j.zeka.starter.messaging.support.MessagingHandlerMethod;
import java.lang.reflect.Method;
import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Kafka 消息监听适配器
 * <p>
 * 该类继承自 AbstractMessagingListenerAdapter，实现了：
 * 1. Kafka 消息的接收处理
 * 2. Kafka 消息到统一消息模型的转换
 * <p>
 * 核心功能：
 * 1. 监听 Kafka 消息
 * 2. 将 ConsumerRecord 转换为 UnifiedMessage
 * 3. 调用业务处理方法
 * <p>
 * 使用场景：
 * 1. Kafka 消息监听器的实现
 * 2. 统一消息处理框架的 Kafka 适配
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class KafkaMessagingListenerAdapter extends AbstractMessagingListenerAdapter {

    /**
     * 构造方法
     *
     * @param handlerMethod 消息处理方法
     * @param context 消息上下文
     * @param method 业务方法
     */
    public KafkaMessagingListenerAdapter(MessagingHandlerMethod handlerMethod,
                                         MessagingContext context,
                                         Method method) {
        super(handlerMethod, context, method);
    }

    /**
     * 处理 Kafka 消息
     *
     * @param record Kafka 消费者记录
     */
    public void onMessage(ConsumerRecord<String, String> record) {
        handleMessage(record);
    }

    /**
     * 创建统一消息对象
     *
     * @param rawMessage 原始消息对象
     * @return 统一消息对象
     */
    @Override
    protected UnifiedMessage createUnifiedMessage(Object rawMessage) {
        ConsumerRecord<String, String> record = (ConsumerRecord<String, String>) rawMessage;
        return new UnifiedMessage(record.topic(), record.value());
    }
}
