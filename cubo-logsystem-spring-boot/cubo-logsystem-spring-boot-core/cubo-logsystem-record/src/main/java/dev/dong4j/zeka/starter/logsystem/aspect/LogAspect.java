package dev.dong4j.zeka.starter.logsystem.aspect;

import dev.dong4j.zeka.starter.logsystem.annotation.OperationLog;
import dev.dong4j.zeka.starter.logsystem.annotation.RestLog;
import dev.dong4j.zeka.starter.logsystem.publisher.ApiLogPublisher;
import dev.dong4j.zeka.starter.logsystem.publisher.SystemLogPublisher;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

/**
 * 日志切面类
 *
 * 该类是日志记录的核心切面，通过AOP拦截标记了日志注解的方法，
 * 使用Spring事件机制异步记录日志到系统中。
 *
 * 主要功能包括：
 * 1. 拦截@RestLog注解的接口方法，记录接口日志
 * 2. 拦截@OperationLog注解的操作方法，记录操作日志
 * 3. 支持SpEL表达式解析日志描述
 * 4. 使用Spring事件机制异步处理日志
 * 5. 自动记录方法执行时间
 *
 * 使用场景：
 * - REST API接口的自动日志记录
 * - 系统操作的审计日志记录
 * - 业务方法的监控日志
 * - 异常和性能的追踪分析
 *
 * 设计意图：
 * 通过AOP切面实现日志记录的自动化，减少业务代码中的日志记录代码，
 * 提供统一的日志记录标准和异步处理能力。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 04:54
 * @since 1.0.0
 */
@Slf4j
@Aspect
@Component
public class LogAspect extends AbstractLoggerDescParser {

    /**
     * 拦截 API, 生成日志
     *
     * @param point   point
     * @param restLog api log
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Around("@annotation(restLog)")
    public Object restLogAround(@NotNull ProceedingJoinPoint point, RestLog restLog) throws Throwable {
        // 获取类名
        String className = point.getTarget().getClass().getName();
        // 获取方法
        String methodName = point.getSignature().getName();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        // 执行方法
        Object result = point.proceed();
        stopWatch.stop();
        // 记录日志
        ApiLogPublisher.publishEvent(methodName, className, restLog, stopWatch.getTotalTimeMillis());
        return result;
    }

    /**
     * 拦截系统敏感操作, 生成日志
     *
     * @param point        point
     * @param operationLog operation log
     * @return the object
     * @throws Throwable throwable
     * @since 1.0.0
     */
    @Around("@annotation(operationLog)")
    public Object errorLogAround(@NotNull ProceedingJoinPoint point, @NotNull OperationLog operationLog) throws Throwable {
        // 执行方法
        Object result;
        Method method = ((MethodSignature) point.getSignature()).getMethod();
        String desc = super.parseDescription(method, point.getArgs(), operationLog.value());
        result = point.proceed();
        // 记录日志
        SystemLogPublisher.publishEvent(operationLog.action(), desc);
        return result;
    }

}
