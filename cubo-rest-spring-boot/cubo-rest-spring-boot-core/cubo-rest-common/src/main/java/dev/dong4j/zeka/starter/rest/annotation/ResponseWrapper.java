package dev.dong4j.zeka.starter.rest.annotation;

import dev.dong4j.zeka.kernel.common.api.R;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 响应结果包装注解
 *
 * 该注解用于标识 Controller 或方法，表示其返回结果需要被自动包装。
 * 被此注解标注的 Controller 或方法可以直接返回实体对象，
 * 最终会通过 {@link R} 对实体进行统一包装。
 *
 * 工作原理：
 * - ResponseWrapperAdvice 会检测被此注解标识的方法
 * - 将方法的返回结果包装成统一的响应格式
 * - 提供一致的 API 响应结构
 *
 * 使用场景：
 * - 需要统一 API 响应格式的接口
 * - 简化 Controller 中的返回值处理
 * - 与前端约定统一的响应结构
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.02.04 11:18
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {ElementType.TYPE, ElementType.METHOD})
@Documented
public @interface ResponseWrapper {
}
