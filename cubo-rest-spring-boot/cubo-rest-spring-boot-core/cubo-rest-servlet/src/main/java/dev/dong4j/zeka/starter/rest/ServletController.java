package dev.dong4j.zeka.starter.rest;

import dev.dong4j.zeka.kernel.common.api.GeneralResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.validation.annotation.Validated;

/**
 * Servlet 环境下的基础 Controller 抽象类
 *
 * 该抽象类为基于 Servlet 技术栈的 Controller 提供了通用的功能和工具方法。
 * 通过继承此类，开发者可以方便地获取 HTTP 请求和响应对象，
 * 同时享受参数验证和通用结果封装等功能。
 *
 * 主要功能：
 * 1. HTTP 请求和响应对象的懒加载获取
 * 2. 参数验证功能的集成（@Validated）
 * 3. 通用结果封装接口的实现（GeneralResult）
 * 4. 线程安全的请求上下文访问
 *
 * 设计特点：
 * - 使用 ObjectFactory 实现懒加载，避免不必要的对象创建
 * - 使用 @Lazy 注解避免循环依赖问题
 * - 提供 protected 方法供子类使用，封装具体实现
 *
 * 使用场景：
 * - REST API Controller 的基类
 * - 需要直接访问 HTTP 请求/响应的场景
 * - 需要参数验证的 Controller
 * - 需要统一结果封装的 API 接口
 *
 * 线程安全：
 * 通过 ObjectFactory 机制确保每个线程获取到的都是当前请求的对象。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.24 20:42
 * @since 1.0.0
 */
@Validated
@SuppressWarnings("SpringJavaAutowiredMembersInspection")
public abstract class ServletController implements GeneralResult {

    /** HTTP 请求对象的工厂，使用懒加载机制 */
    @Autowired
    @Lazy
    protected ObjectFactory<HttpServletRequest> requestFactory;

    /** HTTP 响应对象的工厂，使用懒加载机制 */
    @Autowired
    @Lazy
    protected ObjectFactory<HttpServletResponse> responseFactory;

    /**
     * 获取当前 HTTP 请求对象
     * <p>
     * 该方法通过 ObjectFactory 机制获取当前线程关联的 HTTP 请求对象。
     * 由于使用了懒加载机制，只有在真正需要使用时才会创建对象。
     * <p>
     * 使用场景：
     * - 获取请求参数、请求头等信息
     * - 读取请求体数据
     * - 获取客户端 IP 地址等网络信息
     * - 访问 Session 和 Cookie 数据
     * <p>
     * 注意事项：
     * - 只能在请求处理线程中调用
     * - 需要确保在 HTTP 请求上下文中使用
     *
     * @return 当前的 HTTP 请求对象，不会为 null
     * @since 1.0.0
     */
    protected HttpServletRequest getRequest() {
        return requestFactory.getObject();
    }

    /**
     * 获取当前 HTTP 响应对象
     *
     * 该方法通过 ObjectFactory 机制获取当前线程关联的 HTTP 响应对象。
     * 由于使用了懒加载机制，只有在真正需要使用时才会创建对象。
     *
     * 使用场景：
     * - 设置响应头信息
     * - 设置 HTTP 状态码
     * - 写入响应体数据（二进制数据、文件下载等）
     * - 设置 Cookie 和 Session 属性
     * - 控制缓存策略
     *
     * 注意事项：
     * - 只能在请求处理线程中调用
     * - 需要确保在 HTTP 请求上下文中使用
     * - 对响应的修改必须在响应被提交前完成
     *
     * @return 当前的 HTTP 响应对象，不会为 null
     * @since 1.0.0
     */
    protected HttpServletResponse getResponse() {
        return responseFactory.getObject();
    }

}
