package dev.dong4j.zeka.starter.rest.advice;

import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.exception.GlobalExceptionHandler;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.DispatcherServlet;

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
@Order(Ordered.HIGHEST_PRECEDENCE + 2000)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, Transactional.class})
public class ServletExtendExceptionHandler implements ZekaAutoConfiguration {

    /**
     * 处理数据库唯一键约束异常
     *
     * <p>当尝试插入或更新数据时违反了数据库唯一索引约束时，
     * Spring 会抛出 {@link org.springframework.dao.DuplicateKeyException} 异常，
     * 该方法会捕获此异常并返回友好的错误信息给客户端。</p>
     *
     * @param e DuplicateKeyException 异常实例
     * @return 包含错误信息的统一响应结果
     * @since 1.0.0
     */
    @ExceptionHandler(value = {
        org.springframework.dao.DuplicateKeyException.class
    })
    public Result<Void> handleDuplicateKeyException(@NotNull org.springframework.dao.DuplicateKeyException e) {
        log.warn("已存在被定义为唯一索引的相同数据: [{}]", e.getMessage());
        return R.failed(BaseCodes.FAILURE, "已存在相同的数据");
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
}
