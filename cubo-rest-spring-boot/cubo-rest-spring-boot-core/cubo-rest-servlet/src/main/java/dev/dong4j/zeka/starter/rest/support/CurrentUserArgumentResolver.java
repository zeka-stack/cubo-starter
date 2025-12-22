package dev.dong4j.zeka.starter.rest.support;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.auth.constant.AuthConstant;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import lombok.extern.slf4j.Slf4j;

/**
 * 当前用户参数解析器
 * <p> 用于在 Spring MVC 中解析当前登录用户信息, 将其注入到控制器方法的参数中.
 * 该解析器通过检查参数类型是否为 CurrentUser 类来决定是否进行解析, 并从请求中获取当前登录用户信息.
 * 若未找到用户信息, 则抛出未授权异常.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * 检查是否支持解析指定的方法参数
     * <p>
     * 该方法是 Spring MVC 参数解析器的核心方法之一，用于判断当前解析器是否能够
     * 处理特定的方法参数。只有当方法参数的类型完全匹配 CurrentUser 类时，
     * 该解析器才会被激活来处理参数注入。
     * <p>
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
     * <p>
     * 该方法是参数解析的核心实现，负责从请求上下文中提取当前登录用户的信息，
     * 并将其注入到 Controller 方法的参数中。它依赖于之前拦截器阶段存储的用户信息。
     * <p>
     * 解析流程：
     * 1. 从 NativeWebRequest 中获取存储的用户信息
     * 2. 使用 AuthConstant.CURRENT_LOGIN_USER 作为存储键
     * 3. 检查用户信息是否存在
     * 4. 如果不存在，抛出未授权异常
     * 5. 如果存在，直接返回用户信息对象
     * <p>
     * 存储机制：
     * - 用户信息通过 webRequest.getAttribute() 获取
     * - 存储范围为请求级别（scope = 0，即 REQUEST_SCOPE）
     * - 存储键由 AuthConstant.CURRENT_LOGIN_USER 常量定义
     * <p>
     * 异常情况：
     * - 当用户信息为 null 时，表示用户未认证或认证已过期
     * - 抛出 LowestException，错误码 B.A-40001
     * - 错误信息："未授权"，提示客户端需要重新认证
     * <p>
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
