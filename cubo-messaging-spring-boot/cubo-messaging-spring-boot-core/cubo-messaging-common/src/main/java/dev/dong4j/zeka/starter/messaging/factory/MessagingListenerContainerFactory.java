package dev.dong4j.zeka.starter.messaging.factory;

import dev.dong4j.zeka.starter.messaging.adapter.AbstractMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;

/**
 * 消息监听容器工厂接口
 * <p>
 * 该接口定义了消息监听容器的注册方法，用于：
 * 1. 注册消息监听适配器
 * 2. 根据注解配置初始化监听容器
 * <p>
 * 实现类需要针对不同的消息中间件提供具体实现
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public interface MessagingListenerContainerFactory {
    /**
     * 注册消息监听容器
     *
     * @param adapter 消息监听适配器
     * @param annotation MessagingListener 注解实例
     */
    void registerContainer(AbstractMessagingListenerAdapter adapter, MessagingListener annotation);
}
