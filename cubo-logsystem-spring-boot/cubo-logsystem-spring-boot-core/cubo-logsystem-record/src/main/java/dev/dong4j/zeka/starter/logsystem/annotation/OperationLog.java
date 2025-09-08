package dev.dong4j.zeka.starter.logsystem.annotation;

import dev.dong4j.zeka.starter.logsystem.enums.OperationAction;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 *
 * 该注解用于标记需要记录操作日志的方法，支持SpEL表达式动态生成日志描述。
 * 通过AOP切面自动拦截标记的方法，记录操作日志到系统中。
 *
 * 主要功能包括：
 * 1. 标记需要记录操作日志的方法
 * 2. 支持SpEL表达式动态生成日志描述
 * 3. 支持操作动作类型的指定
 * 4. 与AOP切面配合实现自动日志记录
 *
 * 使用场景：
 * - 系统敏感操作的日志记录
 * - 业务操作的审计日志
 * - 用户行为的追踪记录
 * - 系统操作的监控和分析
 *
 * 设计意图：
 * 通过注解的方式简化操作日志的记录，提供灵活的日志描述生成能力，
 * 支持动态参数解析和操作类型分类。
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
public @interface OperationLog {

    /**
     * 动作
     *
     * @return the operation action
     * @since 1.0.0
     */
    OperationAction action() default OperationAction.DEFAULT;

    /**
     * 操作描述, 支持SpEL表达式调用输出方法中的参数值,
     * 如:\n
     * 1、普通参数phone的值: '用户登录手机号: #{#phone}'
     * 2、对象参数UserDTO的值:' 用户名#{#userDTO.name}, 密码: #{#userDTO.password}'
     * 3、时间expiryDate的值:'有效期: #{#dateFormat(#expiryDate)}'
     *
     * @return {String}
     * @since 1.0.0
     */
    String value() default "操作日志记录";
}
