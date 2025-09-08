package dev.dong4j.zeka.starter.rest.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 原始响应结果注解
 *
 * 该注解用于标识方法，表示该方法的返回结果不需要被包装，直接返回原始值。
 * 主要用于在使用 @RestController 且返回结果为基础类型和 String 时，
 * 需要跳过框架的自动包装机制，直接返回原始值的场景。
 *
 * 与 @ResponseWrapper 注解相反，被 @OriginalResponse 标识的方法不会
 * 被 ResponseWrapperAdvice 处理，直接返回方法的原始返回值。
 *
 * 使用场景：
 * - 返回简单的字符串或数字
 * - 返回文件流或二进制数据
 * - 需要与第三方系统接口保持一致的响应格式
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.04.16 20:39
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OriginalResponse {
}
