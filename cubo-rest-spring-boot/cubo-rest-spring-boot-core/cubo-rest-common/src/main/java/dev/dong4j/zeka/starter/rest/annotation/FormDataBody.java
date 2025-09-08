package dev.dong4j.zeka.starter.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表单数据请求体注解
 *
 * 该注解用于标识方法参数，表示该参数应该从表单数据（form-data）格式的请求体中解析。
 * 主要用于处理 multipart/form-data 类型的请求，如文件上传等场景。
 *
 * 与 @RequestBody 不同，此注解专门用于处理表单数据格式的请求体，
 * 可以更好地支持文件上传和复杂表单数据的绑定。
 *
 * 使用示例：
 * ```java
 * @PostMapping("/upload")
 * public Result<?> upload(@FormDataBody UploadForm form) {
 *     // 处理表单数据
 * }
 * ```
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.08.19 18:00
 * @since 1.0.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FormDataBody {
}
