package dev.dong4j.zeka.starter.rest.filter;

import dev.dong4j.zeka.kernel.auth.constant.AuthConstant;
import dev.dong4j.zeka.kernel.auth.entity.AuthorizationUser;
import dev.dong4j.zeka.kernel.auth.util.JwtUtils;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.kernel.common.util.ObjectUtils;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import dev.dong4j.zeka.kernel.web.support.CacheRequestEnhanceWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

/**
 * 全局参数注入过滤器
 *
 * 该过滤器用于解析 HTTP 请求中的 JWT Token，并自动注入当前用户ID和租户ID参数。
 * 主要用于简化 Controller 方法中获取用户身份信息的过程，避免在每个方法中重复解析 Token。
 *
 * 主要功能：
 * 1. 自动解析 Authorization Header 中的 JWT Token
 * 2. 提取用户身份信息（用户ID、租户ID）
 * 3. 将身份信息作为请求参数注入，可在 Controller 方法中直接使用
 * 4. 支持通过参数名 "currentUserId" 和 "currentTenantId" 自动绑定
 *
 * 工作原理：
 * - 继承 OncePerRequestFilter，确保每次请求只执行一次
 * - 包装原始请求对象，增强参数获取能力
 * - 动态添加 currentUserId 和 currentTenantId 虚拟参数
 * - 在获取这些参数时自动解析 Token 并返回对应值
 *
 * 使用方式：
 * ```java
 * @GetMapping("/user/info")
 * public UserInfo getUserInfo(@RequestParam Long currentUserId,
 *                            @RequestParam Long currentTenantId) {
 *     // currentUserId 和 currentTenantId 会自动从 Token 中解析注入
 *     return userService.getUserInfo(currentUserId, currentTenantId);
 * }
 * ```
 *
 * 安全特性：
 * - Token 解析失败时抛出授权异常
 * - 支持多种 Token 传递方式（Authorization Header、X-Client-Token）
 * - 自动处理请求包装，支持多次读取请求体
 *
 * 注意事项：
 * - 过滤器优先级最低，在其他过滤器之后执行
 * - 依赖 CacheRequestEnhanceWrapper 实现请求体的可重复读取
 * - 仅在需要用户身份信息的接口中使用相关参数名
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.04 11:52
 * @since 1.0.0
 */
@Slf4j
public class GlobalParameterFilter extends OncePerRequestFilter {
    /**
     * 过滤器核心处理方法
     *
     * 该方法是过滤器的入口点，负责包装原始请求对象并添加 Token 解析功能。
     * 根据请求对象的类型选择合适的包装策略，确保请求体可以被多次读取。
     *
     * 处理流程：
     * 1. 检查请求对象类型，确定是否已经被缓存包装
     * 2. 使用 TokenRequestWrapper 包装请求，增加 Token 解析能力
     * 3. 将包装后的请求传递给后续的过滤器链
     *
     * 包装策略：
     * - 如果请求已经是 CacheRequestEnhanceWrapper 类型，直接使用其缓存的请求包装器
     * - 否则创建新的 ContentCachingRequestWrapper 进行包装
     *
     * @param request HTTP 请求对象
     * @param response HTTP 响应对象
     * @param filterChain 过滤器链，用于继续处理请求
     * @throws ServletException 当 Servlet 处理发生异常时抛出
     * @throws IOException 当 I/O 操作发生异常时抛出
     * @since 1.0.0
     */
    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request,
                                 @NotNull HttpServletResponse response,
                                 @NotNull FilterChain filterChain) throws ServletException, IOException {

        if (request instanceof CacheRequestEnhanceWrapper) {
            // 如果请求已经被缓存包装，直接使用其内部的缓存请求包装器
            filterChain.doFilter(new TokenRequestWrapper(((CacheRequestEnhanceWrapper) request).getCachingRequestWrapper()), response);
        } else {
            // 创建新的内容缓存请求包装器，确保请求体可以被多次读取
            filterChain.doFilter(new TokenRequestWrapper(new ContentCachingRequestWrapper(request)), response);
        }
    }

    /**
     * Token 参数解析请求包装器
     *
     * 该内部类继承自 CacheRequestEnhanceWrapper，增强了请求参数获取的能力。
     * 主要功能是在原有请求参数的基础上，动态添加从 JWT Token 中解析出的
     * 用户身份信息参数（currentUserId 和 currentTenantId）。
     *
     * 实现原理：
     * 1. 重写 getParameterNames() 方法，在有 Token 时动态添加虚拟参数名
     * 2. 重写 getParameterValues() 方法，在获取虚拟参数时解析 Token
     * 3. 支持多种 Token 传递方式（Authorization Header 和 X-Client-Token）
     *
     * 虚拟参数：
     * - currentUserId: 当前登录用户的 ID
     * - currentTenantId: 当前用户所属租户的 ID
     *
     * 错误处理：
     * - Token 不存在或解析失败时抛出授权异常
     * - 记录详细的错误日志，方便问题排查
     *
     * 使用场景：
     * 主要用于 Spring MVC 的参数绑定机制，当 Controller 方法参数为 POJO 类型时，
     * Spring 会遍历所有请求参数并对 POJO 属性进行赋值。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2019.12.25 23:51
     * @since 1.0.0
     */
    private static class TokenRequestWrapper extends CacheRequestEnhanceWrapper {
        /** 当前用户ID参数名常量 */
        private static final String CURRENT_USER_ID = "currentUserId";
        /** 当前租户ID参数名常量 */
        private static final String CURRENT_TENANT_ID = "currentTenantId";

        /**
         * 构造方法，初始化 Token 请求包装器
         *
         * 接收一个已经被内容缓存包装的请求对象，
         * 在其基础上添加 Token 解析和参数注入功能。
         *
         * @param request 已经被内容缓存包装的请求对象
         * @since 1.0.0
         */
        TokenRequestWrapper(ContentCachingRequestWrapper request) {
            super(request);
        }

        /**
         * 获取所有请求参数名集合（包括虚拟参数）
         *
         * 该方法被 Spring MVC 用于参数绑定，特别是当 Controller 方法参数为 POJO 类型时。
         * Spring 会遍历所有参数名，并尝试对 POJO 的对应属性进行赋值。
         *
         * 增强功能：
         * 1. 获取原始请求中的所有参数名
         * 2. 检查是否存在有效的 JWT Token
         * 3. 如果 Token 存在，动态添加 currentUserId 和 currentTenantId 参数名
         * 4. 返回完整的参数名列表供 Spring 使用
         *
         * 注意事项：
         * - 只有在检测到有效 Token 时才会添加虚拟参数
         * - 返回的是不可修改的枚举对象，确保线程安全
         *
         * @return 包含所有参数名（包括虚拟参数）的枚举对象
         * @since 1.0.0
         */
        @Override
        public Enumeration<String> getParameterNames() {
            Enumeration<String> enumeration = super.getParameterNames();
            ArrayList<String> list = Collections.list(enumeration);
            // 获取当前请求中的 Token
            String token = this.getToken();
            if (StringUtils.isNotBlank(token)) {
                // 如果 Token 存在且不为空，添加虚拟参数名
                list.add(CURRENT_USER_ID);
                list.add(CURRENT_TENANT_ID);
                return Collections.enumeration(list);
            } else {
                // Token 不存在时，返回原始参数名列表
                return super.getParameterNames();
            }
        }

        private String getToken() {
            // 当有 token 字段时动态的添加 currentUserId 和 currentClientId 字段
            // 优先从 Authorization Header 中获取 JWT Token
            String authentication = JwtUtils.getToken(this.getHeader(AuthConstant.OAUTH_HEADER_TYPE));
            // 如果 Authorization Header 中没有 Token，尝试从 X-Client-Token Header 获取
            return StringUtils.isBlank(authentication) ? this.getHeader(AuthConstant.X_CLIENT_TOKEN) : authentication;
        }

        /**
         * 获取指定参数的值数组（支持虚拟参数）
         *
         * 该方法是 Spring MVC 参数绑定的核心方法，当 Controller 方法需要获取参数值时会调用。
         * 对于虚拟参数（currentUserId 和 currentTenantId），会自动从 JWT Token 中解析。
         *
         * 处理逻辑：
         * 1. 检查是否为虚拟参数（currentUserId 或 currentTenantId）
         * 2. 如果是虚拟参数，获取并解析 JWT Token
         * 3. 从 Token 中提取用户信息，返回对应的用户ID或租户ID
         * 4. 如果不是虚拟参数，委托给父类处理
         *
         * 异常处理：
         * - Token 不存在时抛出“请求未授权”异常
         * - Token 解析失败时抛出“请求未授权”异常
         * - 记录详细的错误日志，包含请求路径和参数名
         *
         * 返回格式：
         * - 返回字符串数组，即使只有一个值也会封装成数组
         * - 这符合 Servlet API 的设计，支持多值参数
         *
         * @param name 参数名，可能是原始参数或虚拟参数
         * @return 参数值数组，对于虚拟参数会从 Token 中解析
         * @throws LowestException 当 Token 不存在或解析失败时抛出
         * @since 1.0.0
         */
        @Override
        @SuppressWarnings("checkstyle:ReturnCount")
        public String[] getParameterValues(String name) {
            if (CURRENT_USER_ID.equals(name) || CURRENT_TENANT_ID.equals(name)) {
                // 对于虚拟参数，需要从 Token 中解析
                String token = this.getToken();
                if (StringUtils.isNotBlank(token)) {
                    // Token 存在，尝试解析用户信息
                    AuthorizationUser user = JwtUtils.PlayGround.getUser(token);
                    if (ObjectUtils.isNull(user)) {
                        // Token 解析失败，记录错误日志并抛出异常
                        log.error("[{}] 使用了 [{}] 或 [{}] 参数名, 但是解析 token 失败",
                            this.cachingRequestWrapper.getPathInfo(),
                            CURRENT_USER_ID,
                            CURRENT_TENANT_ID);
                        throw new LowestException("B.A-40001", "请求未授权");
                    }

                    // 根据参数名返回对应的用户信息
                    if (CURRENT_USER_ID.equals(name)) {
                        return new String[]{String.valueOf(user.getId())};
                    } else {
                        return new String[]{String.valueOf(user.getTenantId())};
                    }
                } else {
                    // Token 不存在，记录错误日志并抛出异常
                    log.error("[{}] 使用了 [{}] 或 [{}] 参数名, 但是未获取到 token",
                        this.cachingRequestWrapper.getPathInfo(),
                        CURRENT_USER_ID,
                        CURRENT_TENANT_ID);

                    throw new LowestException("B.A-40001", "请求未授权");
                }
            }
            // 不是虚拟参数，委托给父类处理
            return super.getParameterValues(name);
        }
    }
}
