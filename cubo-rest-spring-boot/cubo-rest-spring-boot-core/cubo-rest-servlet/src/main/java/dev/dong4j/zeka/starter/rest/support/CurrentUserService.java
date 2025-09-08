package dev.dong4j.zeka.starter.rest.support;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.auth.util.AuthUtils;
import dev.dong4j.zeka.kernel.auth.util.JwtUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;

/**
 * 当前用户信息服务接口
 *
 * 该接口定义了获取当前用户信息的标准方法，主要用于在认证和授权过程中
 * 从 HTTP 请求或 JWT Token 中解析出用户身份信息。
 *
 * 主要功能：
 * 1. 从 JWT Token 中解析用户信息
 * 2. 从 HTTP 请求中提取并解析用户信息
 * 3. 提供默认实现，简化使用
 * 4. 支持自定义实现，满足特定需求
 *
 * 设计特点：
 * - 使用接口设计，提供灵活的扩展能力
 * - 提供默认方法实现，减少样板代码
 * - 集成框架的核心组件，保证一致性
 *
 * 默认实现：
 * - 基于 JwtUtils 进行 Token 解析
 * - 基于 AuthUtils 进行请求头提取
 * - 自动处理各种边界情况
 *
 * 使用方式：
 * ```java
 * // 直接使用默认实现
 * @Component
 * public class DefaultCurrentUserService implements CurrentUserService {
 *     // 使用默认方法，无需额外实现
 * }
 *
 * // 自定义实现
 * @Component
 * public class CustomCurrentUserService implements CurrentUserService {
 *     @Override
 *     public CurrentUser getCurrentUser(String token) {
 *         // 自定义解析逻辑
 *     }
 * }
 * ```
 *
 * 集成组件：
 * - AuthenticationInterceptor：用于认证拦截
 * - CurrentUserArgumentResolver：用于参数解析
 * - GlobalParameterFilter：用于参数注入
 *
 * 扩展点：
 * 子类可以重写默认方法，实现自定义的用户信息获取逻辑，
 * 如集成第三方认证服务、增加缓存机制等。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.09.11 13:06
 * @since 1.0.0
 */
public interface CurrentUserService {

    /**
     * 通过 JWT Token 获取当前用户信息
     *
     * 该方法提供了从 JWT Token 中解析用户信息的默认实现。
     * 使用框架内置的 JwtUtils 工具类进行 Token 解析和验证。
     *
     * 处理流程：
     * 1. 验证 Token 的格式和签名
     * 2. 检查 Token 的有效期
     * 3. 从 Token 的 payload 中提取用户信息
     * 4. 将信息封装为 CurrentUser 对象
     *
     * 支持的 Token 格式：
     * - 标准的 JWT Token（Header.Payload.Signature）
     * - 必须包含用户ID、租户ID 等必要信息
     *
     * 异常处理：
     * - Token 格式错误时返回 null
     * - Token 过期时返回 null
     * - 签名验证失败时返回 null
     *
     * @param token JWT Token 字符串，不能为 null
     * @return 解析成功的用户信息，失败时返回 null
     * @since 1.0.0
     */
    default CurrentUser getCurrentUser(String token) {
        return JwtUtils.PlayGround.getUser(token);
    }

    /**
     * 从 HTTP 请求中获取当前用户信息
     *
     * 该方法提供了从 HTTP 请求头中提取 Token 并解析用户信息的默认实现。
     * 使用框架内置的 AuthUtils 工具类进行请求头解析。
     *
     * 处理流程：
     * 1. 从请求头中提取 Token（支持多种格式）
     * 2. 检查 Token 是否存在且不为空
     * 3. 调用 getCurrentUser(String token) 方法进行解析
     * 4. 返回解析结果
     *
     * 支持的 Token 传递方式：
     * - Authorization Header: "Bearer <token>"
     * - Authorization Header: "<token>"（直接传递）
     * - X-Client-Token Header: "<token>"
     * - 其他框架支持的方式
     *
     * 异常处理：
     * - 请求头中没有 Token 时返回 null
     * - Token 为空字符串时返回 null
     * - Token 解析失败时返回 null
     *
     * 使用场景：
     * - AuthenticationInterceptor 中的认证检查
     * - CurrentUserArgumentResolver 中的参数解析
     * - 其他需要获取当前用户信息的场景
     *
     * @param request HTTP 请求对象，包含请求头信息
     * @return 解析成功的用户信息，失败或未找到 Token 时返回 null
     * @since 1.0.0
     */
    default CurrentUser getCurrentUser(@NotNull HttpServletRequest request) {
        // 从 HTTP 请求头中提取 Token
        String token = AuthUtils.getToken(request);

        if (StringUtils.isNotBlank(token)) {
            // Token 存在且不为空，调用 Token 解析方法
            return this.getCurrentUser(token);
        }
        // Token 不存在或为空，返回 null
        return null;
    }
}
