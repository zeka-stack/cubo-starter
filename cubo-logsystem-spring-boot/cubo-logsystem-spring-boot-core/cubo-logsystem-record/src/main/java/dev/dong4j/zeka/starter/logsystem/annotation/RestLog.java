package dev.dong4j.zeka.starter.logsystem.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 接口日志注解
 *
 * 该注解用于标记需要记录接口日志的方法，主要用于REST API的日志记录。
 * 通过AOP切面自动拦截标记的方法，记录接口调用的详细信息。
 *
 * 主要功能包括：
 * 1. 标记需要记录接口日志的方法
 * 2. 支持自定义日志描述
 * 3. 自动记录接口调用时间
 * 4. 与AOP切面配合实现自动日志记录
 *
 * 使用场景：
 * - REST API接口的日志记录
 * - Web服务调用的监控
 * - 接口性能分析
 * - 接口调用的审计追踪
 *
 * 设计意图：
 * 通过注解的方式简化接口日志的记录，提供统一的接口调用监控能力，
 * 支持接口性能分析和问题排查。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:54
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestLog {

    /**
     * 日志描述
     *
     * @return {String}
     * @since 1.0.0
     */
    String value() default "接口日志记录";
}
