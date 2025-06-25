package dev.dong4j.zeka.starter.messaging.support;

import dev.dong4j.zeka.starter.messaging.context.MessagingContext;
import dev.dong4j.zeka.starter.messaging.model.UnifiedMessage;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * 统一消息处理方法封装，负责调用业务方法
 */
public class MessagingHandlerMethod {

    @Getter
    private final Object bean;
    @Getter
    private final Method method;
    private final UnifiedMessageResolver resolver;
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    public MessagingHandlerMethod(Object bean, Method method, UnifiedMessageResolver resolver) {
        this.bean = bean;
        this.method = method;
        this.resolver = resolver;
    }

    /**
     * 调用业务方法处理消息
     *
     * @param context 消息上下文
     */
    public void invoke(MessagingContext context) {
        try {
            UnifiedMessage unifiedMessage = context.getMessage();
            Object[] args = resolveArguments(context);
            method.invoke(bean, args);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException("Failed to invoke handler method", e);
        } catch (InvocationTargetException e) {
            handleInvocationException(e.getTargetException(), context);
        }
    }

    /**
     * 解析方法参数
     */
    private Object[] resolveArguments(MessagingContext context) {
        List<Object> args = new ArrayList<>();
        int parameterCount = method.getParameterCount();

        for (int i = 0; i < parameterCount; i++) {
            MethodParameter methodParam = new MethodParameter(method, i);
            methodParam.initParameterNameDiscovery(parameterNameDiscoverer);

            Class<?> paramType = methodParam.getParameterType();

            if (paramType == UnifiedMessage.class) {
                args.add(context.getMessage());
            } else if (paramType == MessagingContext.class) {
                args.add(context);
            } else if (paramType == String.class && "payload".equals(methodParam.getParameterName())) {
                args.add(context.getMessage().getPayload());
            } else {
                args.add(resolveArgumentByExpression(methodParam, context));
            }
        }

        return args.toArray();
    }

    /**
     * 使用表达式解析参数
     */
    private Object resolveArgumentByExpression(MethodParameter parameter, MessagingContext context) {
        UnifiedMessageResolver.ArgumentResolverConfig config = resolver.getResolverConfig(method, parameter);
        if (config == null || config.expression() == null) {
            return null;
        }

        try {
            Expression expression = expressionParser.parseExpression(config.expression());
            return expression.getValue(context);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to resolve argument with expression: " + config.expression(), e);
        }
    }

    /**
     * 处理方法调用异常
     */
    private void handleInvocationException(Throwable ex, MessagingContext context) {
        // 根据不同的MQ类型实现不同的异常处理策略
        switch (context.getMessagingType()) {
            case KAFKA:
                handleKafkaException(ex, context);
                break;
            case ROCKETMQ:
                handleRocketMQException(ex, context);
                break;
            case TONGHTP:
                handleTonghtpException(ex, context);
                break;
            default:
                throw new RuntimeException("Unhandled exception in message handler", ex);
        }
    }

    private void handleKafkaException(Throwable ex, MessagingContext context) {
        // Kafka特定的异常处理逻辑
        resolver.onError(ex, context);
    }

    private void handleRocketMQException(Throwable ex, MessagingContext context) {
        // RocketMQ特定的异常处理逻辑
        resolver.onError(ex, context);
    }

    private void handleTonghtpException(Throwable ex, MessagingContext context) {
        // TongHTP特定的异常处理逻辑
        resolver.onError(ex, context);
    }

    /**
     * 方法参数解析配置接口
     */
    public interface UnifiedMessageResolver {
        ArgumentResolverConfig getResolverConfig(Method method, MethodParameter parameter);

        void onError(Throwable ex, MessagingContext context);

        class ArgumentResolverConfig {
            private final String expression;

            public ArgumentResolverConfig(String expression) {
                this.expression = expression;
            }

            public String expression() {
                return expression;
            }
        }
    }
}
