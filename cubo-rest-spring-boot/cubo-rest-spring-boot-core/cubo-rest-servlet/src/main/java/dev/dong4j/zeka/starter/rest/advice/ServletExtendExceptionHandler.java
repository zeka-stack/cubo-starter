package dev.dong4j.zeka.starter.rest.advice;

import dev.dong4j.zeka.kernel.common.api.BaseCodes;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import jakarta.servlet.Servlet;
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
 * <p>Description: 异常全局处理扩展 </p>
 *
 * @author dong4j
 * @version 1.2.3
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
     * Handle duplicate key exception result
     *
     * @param e e
     * @return the result
     * @since 1.0.0
     */
    @ExceptionHandler(value = {
        org.springframework.dao.DuplicateKeyException.class
    })
    public Result<Void> handleDuplicateKeyException(@NotNull Exception e) {
        log.warn("已存在被定义为唯一索引的相同数据: [{}]", e.getMessage());
        return R.failed(BaseCodes.FAILURE, "已存在相同的数据");
    }
}
