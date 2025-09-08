package dev.dong4j.zeka.starter.rest.interceptor;

import dev.dong4j.zeka.kernel.common.context.Trace;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 链路追踪拦截器
 *
 * 该拦截器负责管理请求的链路追踪 ID（traceId），为每个 HTTP 请求生成或传递唯一的追踪标识。
 * 它是分布式系统中实现请求链路追踪的关键组件，帮助开发人员进行问题排查、
 * 性能监控和日志关联分析。
 *
 * 主要功能：
 * 1. 请求开始时生成或获取 traceId
 * 2. 在整个请求处理过程中保持 traceId 的上下文传递
 * 3. 请求结束时自动清理 traceId，防止内存泄露
 * 4. 支持与分布式追踪系统（如 Zipkin、Jaeger）集成
 * 5. 为日志系统提供统一的请求标识
 *
 * 工作流程：
 * 1. 请求预处理（preHandle）：
 *    - 检查当前上下文中是否已存在 traceId
 *    - 如果不存在，生成一个新的 UUID 作为 traceId
 *    - 将 traceId 设置到线程本地存储中
 *
 * 2. 请求完成后处理（afterCompletion）：
 *    - 检查处理器是否为 HandlerMethod（Controller 方法）
 *    - 如果是，从线程本地存储中清除 traceId
 *    - 防止线程池中的线程重用时出现数据污染
 *
 * traceId 生成策略：
 * - 默认使用 UUID 作为 traceId
 * - 如果系统集成了专业的追踪组件（如 Spring Cloud Sleuth），可能会被替换
 * - 支持从上游服务传递的 traceId，实现全链路追踪
 *
 * 执行优先级：
 * - 实现 Ordered 接口，设置高优先级（HIGHEST_PRECEDENCE + 100）
 * - 确保在其他拦截器之前执行，为后续的拦截器和业务逻辑提供 traceId
 * - 在 afterCompletion 中最后清理，确保其他组件能够正常使用 traceId
 *
 * 使用场景：
 * - 分布式系统的请求链路追踪
 * - 日志聚合和关联分析
 * - 性能监控和问题排查
 * - API 调用链的全链路跟踪
 * - 微服务之间的调用关系分析
 *
 * 与其他组件的协同：
 * - 日志系统：在日志中自动包含 traceId，实现日志关联
 * - 异常处理：在异常信息中包含 traceId，便于问题定位
 * - 微服务调用：在服务间调用中传递 traceId
 * - 数据库操作：在数据库日志中包含 traceId
 *
 * 线程安全：
 * - 使用 ThreadLocal 存储 traceId，确保线程安全
 * - 在请求结束时自动清理，防止内存泄露
 * - 支持异步处理和线程池环境
 *
 * 注意事项：
 * - 该拦截器应该在所有其他业务拦截器之前执行
 * - 在异步处理中需要手动传递 traceId
 * - 如果使用了专业的追踪组件，建议禁用该拦截器以避免冲突
 * - 请求结束后必须清理 ThreadLocal，防止内存泄露
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.12.02 01:31
 * @since 1.0.0
 */
@Slf4j
public class TraceInterceptor implements HandlerInterceptor, Ordered {

    /**
     * 请求预处理，初始化或获取 traceId
     *
     * 该方法在请求进入 Controller 之前被调用，负责为当前请求初始化链路追踪 ID。
     * 它会检查当前线程上下文中是否已经存在 traceId，如果不存在则生成一个新的。
     *
     * 处理逻辑：
     * 1. 从线程本地存储中获取当前的 traceId
     * 2. 如果 traceId 为空或空字符串，生成一个新的 UUID
     * 3. 将 traceId 设置到线程本地存储中
     * 4. 后续的所有处理都可以使用这个 traceId
     *
     * traceId 来源：
     * - 从上游服务传递而来（如通过 HTTP 头）
     * - 由专业的追踪组件生成（如 Spring Cloud Sleuth）
     * - 当前方法生成的新 UUID
     *
     * @param request  HTTP 请求对象，本方法中未使用
     * @param response HTTP 响应对象，本方法中未使用
     * @param handler  请求处理器对象，本方法中未使用
     * @return 总是返回 true，允许请求继续处理
     * @since 1.0.0
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
     * 请求完成后清理 traceId
     *
     * 该方法在请求完全处理完成后被调用，负责清理线程本地存储中的 traceId，
     * 防止在线程池环境中出现数据污染和内存泄露。它只对 Controller 方法执行清理操作。
     *
     * 清理条件：
     * - 只有当 handler 是 HandlerMethod 类型时才执行清理
     * - HandlerMethod 代表 Controller 中的业务方法
     * - 这样可以防止对静态资源请求等非业务请求进行不必要的清理
     *
     * 清理重要性：
     * - 防止线程池中的线程重用时带有上一个请求的 traceId
     * - 避免 ThreadLocal 内存泄露，特别是在高并发场景下
     * - 确保每个请求都有独立、清晰的 traceId
     *
     * 执行时机：
     * - 在所有处理都完成后执行，包括特殊情况下（异常、返回等）
     * - 保证在请求的整个生命周期中 traceId 都可用
     * - 在线程返回线程池之前完成清理
     *
     * @param request  HTTP 请求对象，本方法中未使用
     * @param response HTTP 响应对象，本方法中未使用
     * @param handler  请求处理器对象，用于判断是否为 Controller 方法
     * @param ex       处理过程中可能出现的异常，本方法中未使用
     * @since 1.0.0
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
     * 获取拦截器的执行优先级
     *
     * 该方法定义了拦截器在 Spring MVC 拦截器链中的执行顺序。
     * 链路追踪拦截器需要在其他业务拦截器之前执行，以确保所有后续处理
     * 都能获取到正确的 traceId。
     *
     * 优先级设计：
     * - 使用 HIGHEST_PRECEDENCE + 100，确保高优先级
     * - 比统的安全、认证等拦截器优先级更高
     * - 但略低于某些基础组件（如编码、CORS 等）
     * - 保证在大部分情况下都能最先执行
     *
     * 执行顺序重要性：
     * - traceId 必须在日志记录之前初始化
     * - 必须在异常处理之前初始化，以便异常日志包含 traceId
     * - 必须在业务逻辑之前初始化，以便业务日志包含 traceId
     *
     * @return 拦截器的执行优先级，数值越小优先级越高
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 100;
    }
}
