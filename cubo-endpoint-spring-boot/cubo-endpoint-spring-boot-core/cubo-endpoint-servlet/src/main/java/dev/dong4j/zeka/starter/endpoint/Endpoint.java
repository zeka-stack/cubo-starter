package dev.dong4j.zeka.starter.endpoint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.stereotype.Component;

/**
 * 自定义 Endpoint 注解
 *
 * 与 @Controller 功能类似，但仅用于框架提供的端点，以避免与业务代码中
 * 使用 @Controller 定义的端点发生冲突。
 *
 * 可与 @RequestMapping 和所有其他 @Controller 功能一起使用，
 * 并与 Servlet 上下文中的 EndpointHandlerMapping 进行匹配。
 *
 * 被该注解标记的类会被自动注册为 Spring 的组件。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.30 15:10
 * @since 1.0.0
 */
@Component
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Endpoint {

}
