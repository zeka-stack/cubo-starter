package dev.dong4j.zeka.starter.rest.annotation;

import dev.dong4j.zeka.starter.rest.support.SubClassType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 抽象表单请求参数注解
 *
 * 该注解用于解析 JSON 格式的抽象类参数，可直接注入单个参数到 Controller 中。
 * 主要用于处理基于类型判断的抽象表单绑定场景。
 *
 * 工作原理：
 * 1. 根据指定的枚举类型判断具体的子类类型
 * 2. 从 JSON 请求体中解析出对应的类型标识字段
 * 3. 根据类型标识将 JSON 数据反序列化为对应的子类对象
 *
 * 使用场景：
 * - 表单提交中有多种不同类型的数据结构
 * - 根据类型字段动态选择不同的实体类进行数据绑定
 * - 支持多态表单处理机制
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
public @interface RequestAbstractForm {

    /**
     * 指定抽象类的需要被绑定的类型的枚举
     *
     * 此枚举必须实现 SerializeEnum 接口，用于指示如何从 JSON 中
     * 解析出具体的子类类型标识，并据此选择对应的实体类。
     *
     * @return 子类类型枚举类
     * @since 1.0.0
     */
    Class<? extends SubClassType> value();

    /**
     * 是否为必需参数
     *
     * 如果设置为 true，则在请求中未找到对应参数时会抛出异常。
     * 默认为 true，表示该参数是必需的。
     *
     * @return 是否为必需参数
     * @since 1.0.0
     */
    boolean required() default true;
}
