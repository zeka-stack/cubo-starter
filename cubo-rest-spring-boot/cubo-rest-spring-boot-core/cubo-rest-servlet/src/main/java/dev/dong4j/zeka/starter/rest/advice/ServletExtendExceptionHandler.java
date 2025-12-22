package dev.dong4j.zeka.starter.rest.advice;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.exception.GlobalExceptionHandler;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>Description: Servlet 异常全局处理扩展，用于处理 Web 层特定异常</p>
 *
 * <p>该处理器专门处理 Spring Web 层抛出的异常，包括数据库唯一约束冲突等常见异常。
 * 通过 {@link RestControllerAdvice} 注解，该类会自动拦截并处理所有被
 * {@link ExceptionHandler} 标注的方法所声明的异常类型。</p>
 *
 * <p>该异常处理器具有最高优先级（{@link Ordered#HIGHEST_PRECEDENCE} + 2000），
 * 确保在其他异常处理器之前执行。</p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.11 12:32
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE + 500)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, Transactional.class})
public class ServletExtendExceptionHandler implements ZekaAutoConfiguration {

    /**
     * Servlet extend exception handler
     *
     * @since 2024.2.0
     */
    public ServletExtendExceptionHandler() {
        log.info("加载 ServletExtendExceptionHandler");
    }

    /**
     * 处理通用异常并提供详细的错误响应
     *
     * <p>当发生异常时，该方法会记录详细错误日志，并通过
     * {@link GlobalExceptionHandler#result(Exception, HttpServletRequest, String)} 方法
     * 生成包含请求信息和错误详情的统一响应结果。</p>
     *
     * @param e   捕获到的异常实例
     * @param req HTTP 请求对象，用于获取请求相关信息
     * @return 包含详细错误信息的统一响应结果
     * @since 1.0.0
     */
    @ExceptionHandler(value = {
        org.springframework.dao.DuplicateKeyException.class
    })
    public Result<?> handleDuplicateKeyException(@NotNull Exception e, HttpServletRequest req) {
        log.error("已存在被定义为唯一索引的相同数据", e);
        return GlobalExceptionHandler.result(e, req, "已存在相同的数据");
    }

    /**
     * 死锁异常 不能确认锁异常：可能产生原因：A是大事务，然后指定了B业务，B业务也有个事务，A等B提交事务，B一直无法提交事务，这个事务 上述有执行一遍 就会造成死锁异常
     *
     * @param req 请求：可以记录一些传参的内容。你可以自定义日志与向响应的信息，只是当前没用到。
     * @param e   异常对象。
     * @return 响应实体
     */
    @SuppressWarnings("all")
    @ExceptionHandler(value = CannotAcquireLockException.class)
    public Result<?> cannotAcquireLockException(HttpServletRequest req, NumberFormatException e) {
        log.error("死锁异常：{}，为了避免锁表，请使用'select * from information_schema.INNODB_TRX' 如果锁表了，快速kill 某个trx_mysql_thread_id，以解除锁表",
                  e.getMessage());
        return GlobalExceptionHandler.result(e, req, "锁表异常！请检查管理员处理！");
    }
}
