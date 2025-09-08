package dev.dong4j.zeka.starter.rest.interceptor;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.starter.rest.annotation.TokenRequired;
import dev.dong4j.zeka.starter.rest.support.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Token 认证拦截器
 *
 * 该拦截器用于处理基于注解的 API 认证验证。通过使用 @TokenRequired 注解来标记
 * 需要认证的接口，可以灵活地控制哪些 API 需要认证。相比于接口中直接注入 CurrentUser，
 * 这种方式可以避免在 Swagger 等 API 文档工具中显示不必要的参数说明。
 *
 * 主要功能：
 * 1. 检查 Controller 方法是否使用了 @TokenRequired 注解
 * 2. 从 HTTP 请求中提取和验证 JWT Token
 * 3. 解析 Token 并获取当前用户信息
 * 4. 对未通过认证的请求返回错误响应
 *
 * 工作流程：
 * 1. 在请求到达 Controller 方法之前被调用
 * 2. 检查处理器类型，只处理 HandlerMethod 类型
 * 3. 检查方法上是否有 @TokenRequired 注解
 * 4. 如果需要认证，调用 CurrentUserService 获取当前用户
 * 5. 验证成功则继续处理，失败则抛出异常
 *
 * 优势：
 * - 面向注解的设计，使用简单且明确
 * - 不影响 API 文档的可读性
 * - 灵活控制，可以精确到方法级别
 * - 与框架的其他组件无缝集成
 *
 * 配置要求：
 * - 需要在 Spring MVC 配置中注册该拦截器
 * - 依赖 CurrentUserService 的实现
 * - 需要配置合适的拦截路径
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.29 00:02
 * @since 1.0.0
 */
@AllArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    /** 当前用户信息服务，用于从请求中解析用户身份 */
    private final CurrentUserService currentUserService;


    /**
     * 请求前置处理方法，执行认证验证逻辑
     *
     * 该方法在请求到达 Controller 方法之前被调用，用于执行认证验证逻辑。
     * 通过检查方法上的 @TokenRequired 注解来决定是否需要进行认证。
     *
     * 处理流程：
     * 1. 检查处理器类型，非 HandlerMethod 类型直接通过
     * 2. 获取目标方法并检查是否有 @TokenRequired 注解
     * 3. 如果需要认证，调用 CurrentUserService 获取当前用户
     * 4. 用户信息为空则认证失败，抛出未授权异常
     * 5. 认证成功则返回 true，允许请求继续处理
     *
     * 认证策略：
     * - 只有显式使用 @TokenRequired 注解的方法才需要认证
     * - 未标记的方法将直接通过，不进行任何认证检查
     * - 认证失败时抛出统一的未授权异常
     *
     * 异常处理：
     * - 认证失败时抛出 LowestException，错误码为 "B.A-40001"
     * - 异常会被全局异常处理器捕获并返回统一的错误响应
     *
     * @param request HTTP 请求对象，包含认证信息
     * @param response HTTP 响应对象（本方法中未使用）
     * @param handler 请求处理器，可能是 HandlerMethod 或其他类型
     * @return true 表示认证通过或不需要认证，false 表示认证失败（实际上会抛异常）
     * @throws LowestException 当认证失败时抛出未授权异常
     * @since 1.0.0
     */
    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {

        // 检查处理器类型，如果不是映射到具体方法的请求，直接通过
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        Method method = handlerMethod.getMethod();

        // 检查方法是否有 @TokenRequired 注解，决定是否需要认证
        TokenRequired methodAnnotation = method.getAnnotation(TokenRequired.class);
        // 有 @TokenRequired 注解，需要进行认证验证
        if (methodAnnotation != null) {
            // 从请求中获取当前用户信息
            CurrentUser currentUser = this.currentUserService.getCurrentUser(request);
            if (null == currentUser) {
                // 用户信息为空，认证失败，抛出未授权异常
                throw new LowestException("B.A-40001", "未授权");
            }
        }
        return true;
    }
}

