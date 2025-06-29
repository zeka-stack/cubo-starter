package dev.dong4j.zeka.starter.rest.interceptor;

import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * <p>Description: </p>请求完成后删除 traceId
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.02 01:31
 * @since 2022.1.1
 */
@Slf4j
public class TraceInterceptor implements HandlerInterceptor, Ordered {

    /**
     * Pre handle
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @return the boolean
     * @since 2022.1.1
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        String traceId = Trace.context().get();
        if (StringUtils.isBlank(traceId)) {
            Trace.context().set(StringUtils.getUid());
        }
        return true;
    }

    /**
     * After completion
     *
     * @param request  request
     * @param response response
     * @param handler  handler
     * @param ex       ex
     * @since 2022.1.1
     */
    @Override
    public void afterCompletion(@NotNull HttpServletRequest request,
                                @NotNull HttpServletResponse response,
                                @NotNull Object handler,
                                Exception ex) {
        if (handler instanceof HandlerMethod) {
            Trace.context().remove();
        }
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 2022.1.1
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 100;
    }
}
