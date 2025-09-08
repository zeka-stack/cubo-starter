package dev.dong4j.zeka.starter.launcher.annotation;

import dev.dong4j.zeka.starter.launcher.enums.ApplicationType;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 应用运行类型注解
 *
 * 该注解用于标记应用的运行类型，支持以下类型：
 * 1. STANDALONE - 独立应用
 * 2. WEB - Web应用
 * 3. REACTIVE - 响应式应用
 * 4. BATCH - 批处理应用
 * 5. TASK - 任务应用
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.04.05 18:38
 * @since 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RunningType {

    /**
     * 获取应用运行类型
     *
     * @return 应用运行类型枚举值
     * @since 1.0.0
     */
    ApplicationType value();
}
