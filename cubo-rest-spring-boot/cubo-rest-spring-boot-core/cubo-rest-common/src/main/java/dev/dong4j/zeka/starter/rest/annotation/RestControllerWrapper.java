package dev.dong4j.zeka.starter.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST 控制器组合注解
 *
 * 该注解是一个组合注解，集成了多个常用的 Spring MVC 注解，
 * 用于简化 Controller 层的注解使用。
 *
 * 包含的注解：
 * - @RestController：标识为 REST 控制器
 * - @ResponseWrapper：自动包装返回结果
 * - @RequestMapping：设置请求映射路径
 *
 * 使用优势：
 * 1. 减少注解数量，简化代码
 * 2. 统一的配置方式，减少出错可能
 * 3. 自动包装返回结果，无需手动处理
 *
 * 使用示例：
 * ```java
 * @RestControllerWrapper("/api/user")
 * public class UserController {
 *     // 方法实现
 * }
 * ```
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.04 12:41
 * @since 1.0.0
 */
@Target(value = {ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RestController
@ResponseWrapper
@RequestMapping
public @interface RestControllerWrapper {

    /**
     * 定义映射路径 URL
     *
     * 该值会被 @RequestMapping 注解使用，用于设置 Controller 的基础请求路径。
     * 支持多个路径设置，例如：{"/api/v1/user", "/api/v2/user"}
     *
     * @return 请求路径数组
     * @since 1.0.0
     */
    @AliasFor(annotation = RequestMapping.class, value = "path")
    String[] value() default {};

    /**
     * 定义 Spring Bean 的名称
     *
     * 该值会被 @RestController 注解使用，用于指定 Controller 在 Spring 容器中的 Bean 名称。
     * 如果不指定，Spring 会自动生成一个默认名称。
     *
     * @return Spring Bean 名称
     * @since 1.0.0
     */
    @AliasFor(annotation = RestController.class, value = "value")
    String name() default "";

}
