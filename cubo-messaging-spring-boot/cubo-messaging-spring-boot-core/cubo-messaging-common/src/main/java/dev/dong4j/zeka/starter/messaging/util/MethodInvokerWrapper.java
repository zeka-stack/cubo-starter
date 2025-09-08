package dev.dong4j.zeka.starter.messaging.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 方法调用包装类
 * <p>
 * 该类实现了 InvocationHandler 接口，用于：
 * 1. 包装目标方法的调用
 * 2. 提供动态代理功能
 * <p>
 * 核心功能：
 * 1. 方法调用的统一处理
 * 2. 动态代理生成
 * <p>
 * 使用场景：
 * 1. 消息处理方法调用
 * 2. 需要动态代理的场景
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.27
 * @since 1.0.0
 */
public class MethodInvokerWrapper implements InvocationHandler {
    private final Object targetBean;
    private final Method targetMethod;
    private final MethodInvoker invoker;

    /**
     * 构造方法
     *
     * @param targetBean 目标Bean
     * @param targetMethod 目标方法
     * @param invoker 方法调用器
     */
    public MethodInvokerWrapper(Object targetBean, Method targetMethod, MethodInvoker invoker) {
        this.targetBean = targetBean;
        this.targetMethod = targetMethod;
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoker.invoke(args);
    }

    /**
     * 获取目标方法
     *
     * @return 目标方法
     */
    public Method getMethod() {
        return targetMethod;
    }

    /**
     * 获取代理对象
     *
     * @return 代理对象
     */
    public Object getProxy() {
        Class<?>[] interfaces = {targetMethod.getDeclaringClass()};
        return Proxy.newProxyInstance(
            targetMethod.getDeclaringClass().getClassLoader(),
            interfaces,
            this
        );
    }

    /**
     * 方法调用器接口
     */
    @FunctionalInterface
    public interface MethodInvoker {
        /**
         * 调用方法
         *
         * @param args 方法参数
         * @return 方法返回值
         * @throws Throwable 如果调用过程中发生错误
         */
        Object invoke(Object[] args) throws Throwable;
    }
}
