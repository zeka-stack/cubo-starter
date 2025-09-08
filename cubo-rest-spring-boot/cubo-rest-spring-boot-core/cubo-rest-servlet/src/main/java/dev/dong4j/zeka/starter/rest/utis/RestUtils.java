package dev.dong4j.zeka.starter.rest.utis;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.starter.rest.annotation.ResponseWrapper;
import dev.dong4j.zeka.starter.rest.annotation.RestControllerWrapper;
import java.lang.annotation.Annotation;
import java.util.Objects;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST 模块工具类
 *
 * 该工具类提供了与 REST API 相关的通用工具方法，主要用于在响应包装切面中
 * 判断是否需要对某个 Controller 方法的返回结果进行包装处理。
 *
 * 主要功能：
 * 1. 检查 Controller 类或方法是否使用了需要包装的注解
 * 2. 限制只处理框架指定包路径下的类
 * 3. 提高性能，避免对不相关的类进行处理
 *
 * 设计特点：
 * - 使用 @UtilityClass 注解，确保为工具类
 * - 所有方法都是静态方法，无需实例化
 * - 提供精确的判断逻辑，减少误判
 *
 * 使用场景：
 * - ResponseWrapperAdvice 中的支持判断
 * - 其他需要检查 REST 相关注解的地方
 * - 性能优化相关的检查
 *
 * 注意事项：
 * - 支持检查是基于注解的，不支持动态代理等高级特性
 * - 包路径检查是基于字符串包含判断，可能存在误判
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.12 19:05
 * @since 1.0.0
 */
@UtilityClass
public class RestUtils {

    /**
     * 判断是否需要对返回结果进行包装处理
     *
     * 该方法通过检查 Controller 类和方法上的注解，来判断是否需要对返回结果
     * 进行统一包装。支持多种 REST 相关的注解，包括标准和自定义泣解。
     *
     * 支持的注解：
     * 类级别注解：
     * - @RestController：Spring 标准的 REST 控制器注解
     * - @RestControllerWrapper：框架提供的组合注解
     * - @ResponseWrapper：框架提供的响应包装注解
     *
     * 方法级别注解：
     * - @ResponseBody：Spring 标准的响应体注解
     * - @ResponseWrapper：框架提供的响应包装注解
     *
     * 检查策略：
     * 1. 优先检查类级别注解，如果匹配则返回 true
     * 2. 再检查方法级别注解，如果匹配则返回 true
     * 3. 都不匹配则返回 false
     *
     * 性能优化：
     * - 通过早期返回减少不必要的检查
     * - 使用 instanceof 快速判断，性能高于反射
     *
     * @param returnType 方法返回类型参数，包含方法和类的信息
     * @return true 表示需要包装，false 表示不需要包装
     * @since 1.0.0
     */
    public boolean supportsAdvice(@NotNull MethodParameter returnType) {
        Annotation[] annotations = returnType.getDeclaringClass().getAnnotations();
        if (annotations.length > 0) {
            // 检查类级别注解，如果有支持的注解则需要包装
            for (Annotation annotation : annotations) {
                if (annotation instanceof RestController
                    || annotation instanceof RestControllerWrapper
                    || annotation instanceof ResponseWrapper) {
                    return true;
                }
            }

            // 检查方法级别注解，如果有支持的注解则需要包装
            for (Annotation annotation : Objects.requireNonNull(returnType.getMethod()).getAnnotations()) {
                if (annotation instanceof ResponseBody || annotation instanceof ResponseWrapper) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 检查是否为框架指定包路径下的类
     *
     * 该方法用于限制仅处理框架指定包路径下的类，避免对第三方库
     * 或其他不相关的类进行处理。这样可以提高性能并减少意外的副作用。
     *
     * 检查逻辑：
     * 1. 获取类的包路径
     * 2. 与框架配置的基础包路径进行匹配
     * 3. 使用忽略大小写的包含判断
     *
     * 基础包路径：
     * 由 ConfigDefaultValue.BASE_PACKAGES 常量定义，
     * 通常为框架的核心包路径（如 "dev.dong4j.zeka"）。
     *
     * 使用场景：
     * - 在 ResponseWrapperAdvice 中过滤需要处理的类
     * - 避免对第三方库的 Controller 进行处理
     * - 提高框架的性能和稳定性
     *
     * 注意事项：
     * - 使用字符串包含判断，可能存在误判的情况
     * - 建议项目包结构遵循框架约定，避免命名冲突
     *
     * @param returnType 方法返回类型参数，用于获取类信息
     * @return true 表示是框架指定包路径下的类，false 表示不是
     * @since 1.0.0
     */
    public boolean zekaClass(@NotNull MethodParameter returnType) {
        return StringUtils.containsIgnoreCase(returnType.getDeclaringClass().getPackage().getName(),
            ConfigDefaultValue.BASE_PACKAGES);
    }
}
