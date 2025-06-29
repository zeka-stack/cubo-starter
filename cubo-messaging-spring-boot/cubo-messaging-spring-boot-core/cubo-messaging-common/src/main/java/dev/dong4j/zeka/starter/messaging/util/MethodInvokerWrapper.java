package dev.dong4j.zeka.starter.messaging.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MethodInvokerWrapper implements InvocationHandler {

    private final Object targetBean;
    private final Method targetMethod;
    private final MethodInvoker invoker;

    public MethodInvokerWrapper(Object targetBean, Method targetMethod, MethodInvoker invoker) {
        this.targetBean = targetBean;
        this.targetMethod = targetMethod;
        this.invoker = invoker;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return invoker.invoke(args);
    }

    public Method getMethod() {
        return targetMethod;
    }

    public Object getProxy() {
        Class<?>[] interfaces = {targetMethod.getDeclaringClass()};
        return Proxy.newProxyInstance(
            targetMethod.getDeclaringClass().getClassLoader(),
            interfaces,
            this
        );
    }

    @FunctionalInterface
    public interface MethodInvoker {
        Object invoke(Object[] args) throws Throwable;
    }
}
