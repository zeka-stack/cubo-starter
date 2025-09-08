package dev.dong4j.zeka.starter.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单个参数请求注解
 *
 * 该注解用于从 JSON 请求体中解析单个字段的值，直接注入到 Controller 方法参数中。
 * 与 @RequestParam 不同，该注解专门用于处理 JSON 格式的请求体。
 *
 * 工作原理：
 * 1. 解析 JSON 请求体为 Map 对象
 * 2. 根据指定的 key 获取对应的值
 * 3. 进行类型转换和数据绑定
 * 4. 支持枚举类型的特殊处理
 *
 * 使用场景：
 * - 仅需要 JSON 中的某个字段值
 * - 避免创建完整的 DTO 对象
 * - 简化参数传递和处理
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.03 10:40
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestSingleParam {
    /**
     * 指定要从 JSON 中获取的字段名
     *
     * 该值将作为 key 用于从 JSON 请求体中获取对应的值。
     *
     * @return JSON 中的字段名
     * @since 1.0.0
     */
    String value();

    /**
     * 是否为必需参数
     *
     * 如果设置为 true，则在 JSON 中未找到对应字段或值为 null 时会抛出异常。
     * 默认为 true，表示该参数是必需的。
     *
     * @return 是否为必需参数
     * @since 1.0.0
     */
    boolean required() default true;

    /**
     * 默认值
     *
     * 当在 JSON 中未找到对应字段或值为 null 时，使用的默认值。
     * 仅在 required = false 时生效。
     *
     * @return 默认值字符串
     * @since 1.0.0
     */
    String defaultValue() default "";
}
