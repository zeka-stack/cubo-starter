package dev.dong4j.zeka.starter.rest.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Token 认证必需注解
 *
 * 该注解用于标记需要进行 Token 认证的 API 接口。被此注解标记的方法在执行时，
 * 会被 AuthenticationInterceptor 拦截器检查，确保请求头中包含有效的 JWT Token。
 *
 * 主要功能：
 * 1. 声明式标记需要认证的 API 接口
 * 2. 与 AuthenticationInterceptor 配合实现自动认证检查
 * 3. 精确控制到方法级别，灵活性高
 * 4. 不影响 API 文档的可读性
 *
 * 使用场景：
 * - 用户信息查询 API
 * - 需要用户登录后才能访问的业务接口
 * - 敏感数据操作 API
 * - 个人化功能相关的接口
 *
 * 使用方式：
 * ```java
 * @RestController
 * public class UserController {
 *
 *     // 公开接口，不需要认证
 *     @GetMapping("/users/public")
 *     public List<User> getPublicUsers() {
 *         return userService.getPublicUsers();
 *     }
 *
 *     // 需要认证的接口
 *     @TokenRequired
 *     @GetMapping("/users/profile")
 *     public User getUserProfile() {
 *         return userService.getCurrentUserProfile();
 *     }
 * }
 * ```
 *
 * 认证流程：
 * 1. 客户端在请求头中携带 Authorization 或 X-Client-Token
 * 2. AuthenticationInterceptor 检测到 @TokenRequired 注解
 * 3. 提取并验证 Token 的有效性
 * 4. 解析 Token 获取用户信息
 * 5. 验证成功则继续处理，失败则返回 401 错误
 *
 * 优势：
 * - 声明式设计，代码意图明确
 * - 无侵入性，不影响 API 接口的参数列表
 * - 灵活控制，可以针对具体方法进行认证控制
 * - 与 Swagger 等 API 文档工具兼容性好
 *
 * 注意事项：
 * - 需要配置 AuthenticationInterceptor 拦截器
 * - Token 格式必须符合框架要求
 * - 认证失败会抛出统一的未授权异常
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.09.29 00:00
 * @since 1.0.0
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenRequired {

}
