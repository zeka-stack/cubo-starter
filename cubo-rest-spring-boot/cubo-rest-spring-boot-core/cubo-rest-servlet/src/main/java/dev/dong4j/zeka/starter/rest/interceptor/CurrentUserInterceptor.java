package dev.dong4j.zeka.starter.rest.interceptor;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.auth.constant.AuthConstant;
import dev.dong4j.zeka.starter.rest.advice.ResponseWrapperAdvice;
import dev.dong4j.zeka.starter.rest.support.CurrentUserArgumentResolver;
import dev.dong4j.zeka.starter.rest.support.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 当前用户信息拦截器
 *
 * 该拦截器负责在请求处理过程中自动获取和设置当前登录用户的信息。它在请求进入
 * Controller 之前执行，从 HTTP 请求中解析用户身份信息（通常是 JWT Token），
 * 并将解析后的 {@link CurrentUser} 对象存储在请求属性中，供后续的处理环节使用。
 *
 * 主要功能：
 * 1. 自动从 HTTP 请求中获取用户认证信息（如 JWT Token）
 * 2. 调用 CurrentUserService 解析用户信息
 * 3. 将解析后的用户信息存储在请求属性中
 * 4. 为 CurrentUserArgumentResolver 提供数据源
 * 5. 实现用户信息的上下文传递
 *
 * 工作流程：
 * 1. 请求进入拦截器的 preHandle 方法
 * 2. 委托 CurrentUserService 从请求中解析用户信息
 * 3. 如果解析成功，将 CurrentUser 对象存储在请求属性中
 * 4. 使用 AuthConstant.CURRENT_LOGIN_USER 作为存储键
 * 5. 返回 true，允许请求继续处理
 *
 * 依赖组件：
 * - {@link CurrentUserService}：提供用户信息解析服务
 * - {@link AuthConstant}：定义存储键名常量
 * - {@link CurrentUser}：用户信息的数据结构
 *
 * 与其他组件的协同：
 * - {@link CurrentUserArgumentResolver}：从该拦截器存储的属性中获取用户信息
 * - {@link ResponseWrapperAdvice}：可以在响应包装中使用用户信息
 * - 其他需要用户上下文的组件
 *
 * 特殊处理：
 * - 如果无法解析用户信息（currentUser 为 null），不会抛出异常
 * - 这允许公开接口（不需要认证）正常通过该拦截器
 * - 具体的认证检查由 AuthenticationInterceptor 或其他机制负责
 *
 * 使用场景：
 * - 需要用户上下文的所有 REST API
 * - 微服务之间的用户信息传递
 * - 需要记录用户操作日志的场景
 * - 权限控制和数据过滤
 *
 * 设计特点：
 * - 非侵入性：不强制要求所有请求都必须有用户信息
 * - 统一性：所有需要用户信息的组件都使用相同的存储机制
 * - 可扩展性：支持自定义 CurrentUserService 实现
 * - 高性能：只在需要时才解析用户信息，避免不必要的开销
 *
 * 注意事项：
 * - 该拦截器的执行顺序应该在 AuthenticationInterceptor 之前
 * - 需要确保 CurrentUserService 的实现正确配置
 * - 不应该在该拦截器中进行认证检查，只负责用户信息的解析和存储
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.04 11:36
 * @see ResponseWrapperAdvice
 * @since 1.0.0
 */
@Slf4j
@AllArgsConstructor
public class CurrentUserInterceptor implements HandlerInterceptor {
    /** 当前用户信息服务，用于从 HTTP 请求中解析用户身份信息 */
    private final CurrentUserService currentUserService;

    /**
     * 请求预处理方法，解析并存储当前用户信息
     *
     * 该方法在请求进入 Controller 之前被调用，负责从 HTTP 请求中解析用户身份信息，
     * 并将其存储在请求属性中供后续使用。这是用户信息上下文传递的入口点。
     *
     * 处理流程：
     * 1. 使用注入的 CurrentUserService 从请求中解析用户信息
     * 2. CurrentUserService 会尝试从请求头中获取 JWT Token
     * 3. 如果 Token 存在且有效，解析出 CurrentUser 对象
     * 4. 将 CurrentUser 对象存储在请求属性中
     * 5. 使用 AuthConstant.CURRENT_LOGIN_USER 作为存储键
     *
     * 容错处理：
     * - 如果解析失败或用户未登录（currentUser 为 null），不会抛出异常
     * - 这允许公开 API（不需要认证）正常通过该拦截器
     * - 具体的认证检查由 @TokenRequired 注解和 AuthenticationInterceptor 负责
     *
     * 存储机制：
     * - 使用 HttpServletRequest.setAttribute() 方法存储
     * - 存储范围为单次请求，请求结束后自动清理
     * - 存储键由 AuthConstant.CURRENT_LOGIN_USER 常量定义，保证与其他组件的一致性
     *
     * @param request  HTTP 请求对象，包含请求头、参数等信息
     * @param response HTTP 响应对象，本方法中未使用
     * @param handler  请求处理器对象，通常是 Controller 方法
     * @return 总是返回 true，允许请求继续处理
     * @since 1.0.0
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        CurrentUser currentUser = this.currentUserService.getCurrentUser(request);
        if (currentUser != null) {
            request.setAttribute(AuthConstant.CURRENT_LOGIN_USER, currentUser);
        }

        return true;
    }
}
