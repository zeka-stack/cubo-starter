package dev.dong4j.zeka.starter.rest.support;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.auth.constant.AuthConstant;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.starter.rest.interceptor.CurrentUserInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 当前用户参数解析器
 *
 * 该解析器用于在 Spring MVC 的 Controller 方法中自动注入当前登录用户的信息。通过实现
 * HandlerMethodArgumentResolver 接口，它能够在请求处理过程中识别并解析标记为
 * {@link CurrentUser} 类型的方法参数，从请求上下文中提取用户信息并注入到 Controller 中。
 *
 * 主要功能：
 * 1. 自动识别 Controller 方法中的 CurrentUser 类型参数
 * 2. 从请求上下文中提取当前登录用户信息
 * 3. 将用户信息自动注入到方法参数中
 * 4. 提供统一的用户信息获取方式
 * 5. 简化 Controller 中用户信息的访问逻辑
 *
 * 工作流程：
 * 1. 在请求处理之前，{@link CurrentUserInterceptor} 将用户信息存储到请求属性中
 * 2. Controller 方法调用时，该解析器被激活
 * 3. 检查方法参数类型是否为 CurrentUser
 * 4. 从请求属性中提取存储的用户信息
 * 5. 将用户信息注入到 Controller 方法参数中
 *
 * 依赖关系：
 * - 需要配合 {@link CurrentUserInterceptor} 使用
 * - 依赖 {@link AuthConstant#CURRENT_LOGIN_USER} 常量作为存储键
 * - 用户信息必须在拦截器阶段被正确设置
 *
 * 使用场景：
 * <code>
 * ```java
 * @RestController
 * public class UserController {
 *     @GetMapping("/profile")
 *     public Result<UserProfile> getProfile(CurrentUser currentUser) {
 *         // currentUser 会被自动注入当前登录用户信息
 *         return success(userService.getProfile(currentUser.getUserId()));
 *     }
 * }
 * ```
 * </code>
 * <p>
 * 异常处理：
 * - 如果无法从请求上下文中获取用户信息，会抛出 LowestException
 * - 异常代码：B.A-40001，表示未授权访问
 * - 这通常发生在用户未登录或 Token 无效的情况下
 *
 * 设计特点：
 * - 基于 Spring MVC 的参数解析机制
 * - 类型安全，只处理 CurrentUser 类型的参数
 * - 与框架的认证体系紧密集成
 * - 提供清晰的错误提示和异常处理
 * - 简化 Controller 代码，提高开发效率
 *
 * 注意事项：
 * - 使用该解析器的 Controller 方法必须在已认证的上下文中调用
 * - 需要确保 CurrentUserInterceptor 在该解析器之前执行
 * - 建议配合 @TokenRequired 注解使用，确保请求已通过认证
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.11 13:48
 * @see CurrentUserInterceptor
 * @since 1.0.0
 */
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 检查是否支持解析指定的方法参数
     *
     * 该方法是 Spring MVC 参数解析器的核心方法之一，用于判断当前解析器是否能够
     * 处理特定的方法参数。只有当方法参数的类型完全匹配 CurrentUser 类时，
     * 该解析器才会被激活来处理参数注入。
     *
     * 类型检查逻辑：
     * - 使用 Class.equals() 进行精确的类型匹配
     * - 不支持 CurrentUser 的子类或实现类
     * - 确保类型安全，避免错误的参数注入
     *
     * @param parameter 方法参数的元数据信息，包含参数类型、注解等信息
     * @return 如果参数类型为 CurrentUser 则返回 true，否则返回 false
     * @since 1.0.0
     */
    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return CurrentUser.class.equals(parameter.getParameterType());
    }

    /**
     * 解析方法参数并注入当前用户信息
     *
     * 该方法是参数解析的核心实现，负责从请求上下文中提取当前登录用户的信息，
     * 并将其注入到 Controller 方法的参数中。它依赖于之前拦截器阶段存储的用户信息。
     *
     * 解析流程：
     * 1. 从 NativeWebRequest 中获取存储的用户信息
     * 2. 使用 AuthConstant.CURRENT_LOGIN_USER 作为存储键
     * 3. 检查用户信息是否存在
     * 4. 如果不存在，抛出未授权异常
     * 5. 如果存在，直接返回用户信息对象
     *
     * 存储机制：
     * - 用户信息通过 webRequest.getAttribute() 获取
     * - 存储范围为请求级别（scope = 0，即 REQUEST_SCOPE）
     * - 存储键由 AuthConstant.CURRENT_LOGIN_USER 常量定义
     *
     * 异常情况：
     * - 当用户信息为 null 时，表示用户未认证或认证已过期
     * - 抛出 LowestException，错误码 B.A-40001
     * - 错误信息："未授权"，提示客户端需要重新认证
     *
     * 依赖条件：
     * - CurrentUserInterceptor 必须在此之前执行
     * - 用户必须已通过 JWT Token 验证或其他认证方式
     * - 请求属性中必须包含有效的用户信息
     *
     * @param parameter     方法参数的元数据，包含参数类型、注解等信息
     * @param modelAndView  模型和视图容器，本方法中未使用
     * @param webRequest    Web 请求对象，用于访问请求属性和会话信息
     * @param binderFactory 数据绑定工厂，本方法中未使用
     * @return 当前登录用户的信息对象
     * @throws LowestException 当无法获取用户信息时抛出，错误码为 B.A-40001
     * @since 1.0.0
     */
    @Override
    public Object resolveArgument(@NotNull MethodParameter parameter,
                                  ModelAndViewContainer modelAndView,
                                  @NotNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        Object currentUserInfo = webRequest.getAttribute(AuthConstant.CURRENT_LOGIN_USER, 0);
        if (null == currentUserInfo) {
            throw new LowestException("B.A-40001", "未授权");
        }
        return currentUserInfo;
    }

}
