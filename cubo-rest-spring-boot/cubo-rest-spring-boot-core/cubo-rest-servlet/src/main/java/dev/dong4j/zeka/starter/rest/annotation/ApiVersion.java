package dev.dong4j.zeka.starter.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.web.bind.annotation.Mapping;

/**
 * API 版本控制注解
 *
 * 该注解用于标记 REST API 的版本信息，实现 API 的版本化管理和路由。
 * 通过在 Controller 类或方法上使用此注解，可以实现同一个 API 的多个版本并存，
 * 方便 API 的向下兼容和灰度升级。
 *
 * 主要功能：
 * 1. 支持在 URL 路径中加入版本号（如 /v1/users、/v2/users）
 * 2. 支持多个版本号的批量指定
 * 3. 可以在类和方法级别同时使用，方法级别优先级更高
 * 4. 与 Spring MVC 的请求映射机制集成
 *
 * 使用方式：
 * ```java
 * // 类级别版本控制
 * @ApiVersion({1, 2})
 * @RestController
 * public class UserController {
 *
 *     // 方法级别版本控制，会覆盖类级别设置
 *     @ApiVersion(3)
 *     @GetMapping("/users")
 *     public List<User> getUsers() {
 *         // 该方法只对 v3 版本生效
 *     }
 * }
 * ```
 *
 * URL 路径示例：
 * - /v1/users - 访问版本 1 的 API
 * - /v2/users - 访问版本 2 的 API
 * - /v3/users - 访问版本 3 的 API
 *
 * 版本选择策略：
 * - 当指定多个版本时，会选择最小的版本号作为主版本
 * - 支持版本范围映射，即一个实现可以支持多个版本
 *
 * 注意事项：
 * - 需要配合 ApiVersionRequestMappingHandlerMapping 使用
 * - 版本号必须是正整数，且大于 0
 * - 默认版本为 1，如果不指定则使用默认值
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.26 10:48
 * @since 1.0.0
 */
@Mapping
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface ApiVersion {

    /**
     * 指定 API 的版本号数组
     *
     * 可以指定一个或多个版本号，表示当前 API 支持的版本范围。
     * 当指定多个版本时，该 API 会在所有指定的版本下都可以访问。
     *
     * 示例：
     * - {1}：仅支持版本 1
     * - {1, 2, 3}：同时支持版本 1、2 和 3
     * - {5, 6, 7}：支持版本 5 到 7
     *
     * 版本号要求：
     * - 必须是正整数
     * - 建议从 1 开始递增
     * - 不建议使用过大的版本号
     *
     * 默认值：
     * 如果不显式指定，默认为版本 1。
     *
     * @return API 支持的版本号数组
     * @since 1.0.0
     */
    int[] value() default 1;
}
