// UnifiedListenerRegistry.java
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

public class MessagingListenerRegistry implements BeanPostProcessor {

    private final MessagingRegistrationHandler registrationHandler;
    private final MessagingTypeDetector typeDetector;

    public MessagingListenerRegistry(MessagingRegistrationHandler registrationHandler,
                                     MessagingTypeDetector typeDetector) {
        this.registrationHandler = registrationHandler;
        this.typeDetector = typeDetector;
    }

    /**
     * 在 Bean 初始化完成后执行
     *
     * @param bean     豆
     * @param beanName 豆名称
     * @return {@link Object }
     * @throws BeansException 豆例外
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 获取 Bean 的所有方法
        for (Method method : bean.getClass().getMethods()) {
            // 检查方法是否带有@UnifiedMessageListener注解
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

    public void registerMethod(Object bean, Method method, MessagingListener annotation) {
        MessagingContext context = new MessagingContext();
        context.setMessagingType(typeDetector.resolveType(annotation.type()));
        context.setTopic(annotation.topic());
        context.setGroupId(annotation.groupId());

        // UnifiedMessageHandler handler = createMessageHandler(bean, method);

        // 创建自定义解析器
        MessagingHandlerMethod.UnifiedMessageResolver resolver = createCustomResolver();


        // 创建 UnifiedMessageHandlerMethod
        MessagingHandlerMethod handlerMethod = new MessagingHandlerMethod(bean, method, resolver);

        // 创建适配器
        AbstractMessagingListenerAdapter adapter = createListenerAdapter(handlerMethod, context, method);

        // 注册适配器
        registrationHandler.registerAdapter(adapter, annotation);
    }

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

    private MessagingMessageHandler createMessageHandler(Object bean, Method method) {
        return (message, ctx) -> {
            try {
                method.invoke(bean, message);
            } catch (Exception e) {
                System.err.println("Error processing message: " + e.getMessage());
            }
        };
    }

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
