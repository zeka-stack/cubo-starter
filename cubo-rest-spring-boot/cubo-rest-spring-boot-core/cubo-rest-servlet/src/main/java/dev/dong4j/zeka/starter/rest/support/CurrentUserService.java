package dev.dong4j.zeka.starter.rest.support;

import org.jetbrains.annotations.NotNull;

import dev.dong4j.zeka.kernel.auth.CurrentUser;
import dev.dong4j.zeka.kernel.auth.util.AuthUtils;
import dev.dong4j.zeka.kernel.auth.util.JwtUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 当前用户服务接口
 * <p> 提供获取当前用户信息的功能, 支持通过 token 或 HTTP 请求获取用户信息, 适用于基于 JWT 的身份验证场景.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
public interface CurrentUserService {

    /**
     * 通过 JWT Token 获取当前用户信息
     * <p> 该方法提供了从 JWT Token 中解析用户信息的默认实现. 使用框架内置的 JwtUtils 工具类进行 Token 解析和验证.
     * <p> 处理流程包括验证 Token 的格式和签名, 检查有效期, 从 payload 提取用户信息并封装为 CurrentUser 对象.
     * <p> 若 Token 格式错误, 过期或签名验证失败, 将返回 null.
     *
     * @param token JWT Token 字符串, 不能为 null
     * @return 解析成功的用户信息, 解析失败时返回 null
     * @since 1.0.0
     */
    default CurrentUser getCurrentUser(String token) {
        return JwtUtils.PlayGround.getUser(token);
    }

    /**
     * 从 HTTP 请求中获取当前用户信息
     * <p> 该方法提供了从 HTTP 请求头中提取 Token 并解析用户信息的默认实现. 使用框架内置的 AuthUtils 工具类进行请求头解析.
     * <p> 处理流程:
     * 1. 从请求头中提取 Token(支持多种格式)
     * 2. 检查 Token 是否存在且不为空
     * 3. 调用 getCurrentUser(String token) 方法进行解析
     * 4. 返回解析结果
     * <p> 支持的 Token 传递方式:
     * - Authorization Header: "Bearer token"
     * - Authorization Header: "token"(直接传递)
     * - X-Client-Token Header: "token"
     * - 其他框架支持的方式
     * <p> 异常处理:
     * - 请求头中没有 Token 时返回 null
     * - Token 为空字符串时返回 null
     * - Token 解析失败时返回 null
     *
     * @param request HTTP 请求对象, 包含请求头信息
     * @return 解析成功的用户信息, 失败或未找到 Token 时返回 null
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
