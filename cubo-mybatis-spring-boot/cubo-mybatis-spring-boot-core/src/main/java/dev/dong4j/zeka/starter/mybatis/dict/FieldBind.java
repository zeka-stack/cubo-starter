package dev.dong4j.zeka.starter.mybatis.dict;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2024.05.08 13:35
 * @since 2.0.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface FieldBind {
    /**
     * Target
     *
     * @return the string
     * @since 2.0.0
     */
    String target(); // 翻译的目标属性

    /**
     * spel表达式用于兼容不同场景下字典类型或者 id 类型数据的映射
     *
     * @return the string
     * @see SpelDataBind
     * @since 2024.2.0
     */
    String express() default "";
}
