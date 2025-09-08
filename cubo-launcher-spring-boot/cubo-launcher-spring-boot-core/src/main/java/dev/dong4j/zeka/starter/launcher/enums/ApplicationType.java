package dev.dong4j.zeka.starter.launcher.enums;

import org.springframework.util.ClassUtils;

/**
 * 应用类型枚举，用于定义和识别不同类型的应用
 *
 * 该枚举定义了四种应用类型：
 * 1. NONE - 非 Web 应用，启动完成后自动退出
 * 2. SERVICE - 非 Web 应用，但启动后不会退出（如 Dubbo 服务提供者）
 * 3. SERVLET - 传统 Servlet Web 应用
 * 4. REACTIVE - 响应式 WebFlux 应用
 *
 * 提供了自动检测应用类型的方法，通过分析类路径和应用上下文类型来确定应用类型。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.13 21:15
 * @since 1.0.0
 */
public enum ApplicationType {

    /** 非 web 应用, 启动完成后将自动退出 */
    NONE,
    /**
     * 非 web 应用, 但是启动后不会退出, 比如 dubbo 服务提供者, 只提供 RPC 服务.
     * 这种类型有 2 中情况:
     * 一是 class 存在 web 依赖, 但是启动时关闭了 web 功能 (web 相关的依赖也会被打包)
     * 二是不存在 web 相关依赖, 作为一个普通应用启动, 但是不能退出.
     */
    SERVICE,
    /** web 应用 */
    SERVLET,
    /** webflux 应用 */
    REACTIVE;

    /** SERVLET_INDICATOR_CLASSES */
    private static final String[] SERVLET_INDICATOR_CLASSES = {
        "jakarta.servlet.Servlet",
        "org.springframework.web.context.ConfigurableWebApplicationContext"
    };

    /** WEBMVC_INDICATOR_CLASS */
    private static final String WEBMVC_INDICATOR_CLASS = "org.springframework.web.servlet.DispatcherServlet";
    /** WEBFLUX_INDICATOR_CLASS */
    private static final String WEBFLUX_INDICATOR_CLASS = "org.springframework.web.reactive.DispatcherHandler";
    /** JERSEY_INDICATOR_CLASS */
    private static final String JERSEY_INDICATOR_CLASS = "org.glassfish.jersey.servlet.ServletContainer";
    /** SERVLET_APPLICATION_CONTEXT_CLASS */
    private static final String SERVLET_APPLICATION_CONTEXT_CLASS = "org.springframework.web.context.WebApplicationContext";
    /** REACTIVE_APPLICATION_CONTEXT_CLASS */
    private static final String REACTIVE_APPLICATION_CONTEXT_CLASS =
        "org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext";

    /**
     * 从类路径推断应用类型
     *
     * 通过检查类路径中是否存在特定的指示器类来确定应用类型：
     * 1. 如果存在 WebFlux 指示器类但不存在 WebMVC 和 Jersey 指示器类，则为 REACTIVE 类型
     * 2. 如果不存在任何 Servlet 指示器类，则为 NONE 类型
     * 3. 否则为 SERVLET 类型
     *
     * @return 推断出的应用类型
     * @since 1.0.0
     */
    public static ApplicationType deduceFromClasspath() {
        if (ClassUtils.isPresent(WEBFLUX_INDICATOR_CLASS, null)
            && !ClassUtils.isPresent(WEBMVC_INDICATOR_CLASS, null)
            && !ClassUtils.isPresent(JERSEY_INDICATOR_CLASS, null)) {
            return ApplicationType.REACTIVE;
        }
        for (String className : SERVLET_INDICATOR_CLASSES) {
            if (!ClassUtils.isPresent(className, null)) {
                return ApplicationType.NONE;
            }
        }
        return ApplicationType.SERVLET;
    }

    /**
     * 从应用上下文类推断应用类型
     *
     * 通过检查应用上下文类的类型来确定应用类型：
     * 1. 如果是 WebApplicationContext 的子类，则为 SERVLET 类型
     * 2. 如果是 ReactiveWebApplicationContext 的子类，则为 REACTIVE 类型
     * 3. 否则为 NONE 类型
     *
     * @param applicationContextClass 应用上下文类
     * @return 推断出的应用类型
     * @since 1.0.0
     */
    public static ApplicationType deduceFromApplicationContext(Class<?> applicationContextClass) {
        if (isAssignable(SERVLET_APPLICATION_CONTEXT_CLASS, applicationContextClass)) {
            return ApplicationType.SERVLET;
        }
        if (isAssignable(REACTIVE_APPLICATION_CONTEXT_CLASS, applicationContextClass)) {
            return ApplicationType.REACTIVE;
        }
        return ApplicationType.NONE;
    }

    /**
     * 检查指定类型是否可分配给目标类型
     *
     * 通过类名解析目标类，并检查指定类型是否是目标类的子类或实现类。
     * 该方法处理了可能出现的类加载异常，确保在类不存在时返回 false 而不是抛出异常。
     *
     * @param target 目标类的全限定名
     * @param type   要检查的类型
     * @return 如果指定类型可分配给目标类型则返回 true，否则返回 false
     * @since 1.0.0
     */
    private static boolean isAssignable(String target, Class<?> type) {
        try {
            return ClassUtils.resolveClassName(target, null).isAssignableFrom(type);
        } catch (Throwable ex) {
            return false;
        }
    }

}
