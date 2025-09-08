package dev.dong4j.zeka.starter.messaging.registry;

import dev.dong4j.zeka.starter.messaging.adapter.AbstractMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.adapter.KafkaMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.adapter.RocketMQMessagingListenerAdapter;
import dev.dong4j.zeka.starter.messaging.annotation.MessagingListener;
import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.handler.MessagingMessageHandler;
import dev.dong4j.zeka.starter.messaging.support.MessagingHandlerMethod;
import dev.dong4j.zeka.starter.messaging.util.MessagingTypeDetector;
import java.lang.reflect.Method;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodParameter;

/**
 * 消息监听器注册表
 * <p>
 * 该类实现了 Spring 的 BeanPostProcessor 接口，用于：
 * 1. 扫描带有 @MessagingListener 注解的方法
 * 2. 根据消息类型创建对应的监听适配器
 * 3. 注册监听适配器到相应的消息中间件
 * <p>
 * 核心功能：
 * 1. 自动注册消息监听方法
 * 2. 支持多种消息中间件(Kafka/RocketMQ)
 * 3. 提供统一的异常处理机制
 * <p>
 * 使用场景：
 * 1. Spring Bean 初始化完成后自动处理
 * 2. 消息监听器的统一注册管理
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class MessagingListenerRegistry implements BeanPostProcessor {

    private final MessagingRegistrationHandler registrationHandler;
    private final MessagingTypeDetector typeDetector;

    public MessagingListenerRegistry(MessagingRegistrationHandler registrationHandler,
                                     MessagingTypeDetector typeDetector) {
        this.registrationHandler = registrationHandler;
        this.typeDetector = typeDetector;
    }

    /**
     * Bean 初始化后处理方法
     *
     * @param bean     当前处理的 Bean 实例
     * @param beanName Bean 名称
     * @return 处理后的 Bean 实例
     * @throws BeansException 如果处理过程中发生错误
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取 Bean 的所有方法
        for (Method method : bean.getClass().getMethods()) {
            // 检查方法是否带有 @MessagingListener 注解
            if (method.isAnnotationPresent(MessagingListener.class)) {
                // 获取注解实例
                MessagingListener annotation = method.getAnnotation(MessagingListener.class);
                try {
                    // 1. 验证MQ类型配置
                    typeDetector.validate(annotation);
                    // 2. 注册监听方法
                    registerMethod(bean, method, annotation);
                } catch (Exception e) {
                    // 处理注册异常
                    String errorMsg = String.format("Failed to register listener: %s.%s",
                        bean.getClass().getName(), method.getName());
                    throw new BeanInitializationException(errorMsg, e);
                }
            }
        }
        return bean;
    }

    /**
     * 注册消息监听方法
     *
     * @param bean 包含监听方法的 Bean 实例
     * @param method 带有 @MessagingListener 注解的方法
     * @param annotation MessagingListener 注解实例
     */
    public void registerMethod(Object bean, Method method, MessagingListener annotation) {
        MessagingContext context = new MessagingContext();
        context.setMessagingType(typeDetector.resolveType(annotation.type()));
        context.setTopic(annotation.topic());
        context.setGroupId(annotation.groupId());

        // UnifiedMessageHandler handler = createMessageHandler(bean, method);

        // 创建自定义解析器
        MessagingHandlerMethod.UnifiedMessageResolver resolver = createCustomResolver();

        // 创建 MessagingHandlerMethod, 最终会通过代理调用具体实现处理消息
        MessagingHandlerMethod handlerMethod = new MessagingHandlerMethod(bean, method, resolver);

        // 创建适配器
        AbstractMessagingListenerAdapter adapter = createListenerAdapter(handlerMethod, context, method);

        // 注册适配器
        registrationHandler.registerAdapter(adapter, annotation);
    }

    /**
     * 创建自定义消息解析器
     *
     * @return 自定义消息解析器实例
     */
    private MessagingHandlerMethod.UnifiedMessageResolver createCustomResolver() {
        return new MessagingHandlerMethod.UnifiedMessageResolver() {
            @Override
            public ArgumentResolverConfig getResolverConfig(Method method, MethodParameter parameter) {
                // 实现自定义参数解析逻辑
                return new ArgumentResolverConfig("#message.payload");
            }

            @Override
            public void onError(Throwable ex, MessagingContext context) {
                // 自定义错误处理
                System.err.println("Error processing message: " + ex.getMessage());
            }
        };
    }

    /**
     * 创建消息处理器
     *
     * @param bean 包含处理方法的 Bean 实例
     * @param method 处理方法
     * @return 消息处理器实例
     */
    private MessagingMessageHandler createMessageHandler(Object bean, Method method) {
        return (message, ctx) -> {
            try {
                method.invoke(bean, message);
            } catch (Exception e) {
                System.err.println("Error processing message: " + e.getMessage());
            }
        };
    }

    /**
     * 创建监听器适配器
     *
     * @param handlerMethod 消息处理方法
     * @param context 消息上下文
     * @param method 监听方法
     * @return 消息监听适配器实例
     * @throws IllegalArgumentException 如果消息类型不支持
     */
    private AbstractMessagingListenerAdapter createListenerAdapter(MessagingHandlerMethod handlerMethod,
                                                                   MessagingContext context,
                                                                   Method method) {
        switch (context.getMessagingType()) {
            case KAFKA:
                return new KafkaMessagingListenerAdapter(handlerMethod, context, method);
            case ROCKETMQ:
                return new RocketMQMessagingListenerAdapter(handlerMethod, context, method);
            default:
                throw new IllegalArgumentException("Unsupported MQ type");
        }
    }
}
