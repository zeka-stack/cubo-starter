package dev.dong4j.zeka.starter.endpoint.constant;

/**
 * Endpoint 模块常量定义类
 *
 * 该类定义了 cubo-endpoint 模块中用于标识不同 Web 技术栈的模块名称常量。
 * 包含 Reactive 和 Servlet 两种不同的 Spring Boot Starter 模块名称，
 * 用于在自动配置和 SPI 机制中进行模块识别和加载。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.08 22:01
 * @since 1.0.0
 */
public final class Endpoint {
    /** Reactive 模块名称 */
    public static final String REACTIVE_MODULE_NAME = "cubo-endpoint-reactive-spring-boot-starter";
    /** Servlet 模块名称 */
    public static final String SERVLET_MODULE_NAME = "cubo-endpoint-servlet-spring-boot-starter";
}
