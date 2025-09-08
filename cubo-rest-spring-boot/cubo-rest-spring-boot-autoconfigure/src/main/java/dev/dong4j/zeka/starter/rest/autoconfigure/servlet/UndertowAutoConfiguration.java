package dev.dong4j.zeka.starter.rest.autoconfigure.servlet;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.undertow.ShowUndertowLog;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.ZekaServletExceptionErrorAttributes;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.RequestDumpingHandler;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * Undertow 容器自动配置类
 *
 * 该自动配置类专门用于配置和优化 Undertow Web 服务器。Undertow 是一个高性能的
 * Java Web 服务器，特别适合于微服务和高并发场景。这个配置类提供了 Undertow
 * 容器的各种优化设置和功能增强。
 *
 * 主要功能：
 * 1. WebSocket 支持：解决 WebSocket 缓冲池警告问题
 * 2. HTTP/2 支持：可选择性开启 HTTP/2 协议支持
 * 3. 请求日志：支持详细的 HTTP 请求日志记录
 * 4. 文件上传：配置 multipart 文件上传的临时目录
 * 5. 性能优化：开启请求时间记录，支持访问日志的时间统计
 *
 * 配置特点：
 * - WebSocket 缓冲池：自动配置 DefaultByteBufferPool，避免默认池警告
 * - HTTP/2 协议：通过配置属性控制是否启用
 * - 动态日志：支持运行时开关容器请求日志
 * - 文件上传：自定义临时目录配置
 * - 请求计时：开启 RECORD_REQUEST_START_TIME 支持访问日志中的耗时统计
 *
 * 容器优化：
 * - 解决 UT026010 警告：Buffer pool was not set on WebSocketDeploymentInfo
 * - 支持 HTTP/2 协议，提升传输效率
 * - 提供详细的请求追踪和日志记录
 * - 优化文件上传的临时存储配置
 *
 * 使用场景：
 * - 高并发的 Web 应用
 * - 需要 WebSocket 支持的实时应用
 * - 需要 HTTP/2 支持的现代 Web 应用
 * - 需要详细请求日志的监控场景
 * - 微服务架构中的业务服务
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(RestProperties.class)
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, ZekaServletExceptionErrorAttributes.class, Undertow.class})
public class UndertowAutoConfiguration implements ZekaAutoConfiguration, WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    /** Rest properties */
    private final RestProperties properties;

    /**
     * Undertow 自动配置构造函数
     *
     * 初始化 Undertow 自动配置，接收 REST 配置属性用于后续的容器定制。
     *
     * @param properties REST 模块配置属性，包含 Undertow 相关的配置选项
     * @since 1.0.0
     */
    @Contract(pure = true)
    public UndertowAutoConfiguration(RestProperties properties) {
        this.properties = properties;
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 配置文件上传的临时目录
     *
     * 创建 multipart 文件上传配置，设置 Undertow 容器处理文件上传时的临时存储目录。
     * 这个配置对于文件上传功能的性能和安全性都很重要。
     *
     * 配置作用：
     * - 指定文件上传时的临时存储位置
     * - 避免使用系统默认临时目录可能带来的权限问题
     * - 便于文件上传的监控和管理
     * - 支持大文件上传的磁盘空间规划
     *
     * @return 配置好的 multipart 配置元素
     * @since 1.0.0
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        final String location = this.properties.getMultipart().getLocation();
        log.debug("Undertow 容器目录: [{}]", location);
        factory.setLocation(location);
        return factory.createMultipartConfig();
    }

    /**
     * 自定义 Undertow 服务器配置
     *
     * 这个方法实现了对 Undertow 服务器的深度定制，包括 WebSocket 支持、HTTP/2 支持、
     * 请求日志记录等功能。这些配置主要用于提升服务器性能和增强监控能力。
     *
     * 主要配置项：
     * 1. WebSocket 缓冲池配置：
     *    - 解决 UT026010 警告问题
     *    - 设置 DefaultByteBufferPool，避免使用默认池
     *    - 缓冲区大小设置为 1024 字节
     *
     * 2. HTTP/2 支持：
     *    - 根据配置属性决定是否开启
     *    - 通过 UndertowOptions.ENABLE_HTTP2 选项控制
     *    - 提升 HTTP 传输效率和性能
     *
     * 3. 请求日志功能：
     *    - 添加自定义的请求转储处理器
     *    - 支持动态开关日志记录
     *    - 集成框架的日志显示逻辑
     *
     * 4. 请求计时支持：
     *    - 开启 RECORD_REQUEST_START_TIME 选项
     *    - 支持访问日志中的 %D 时间统计
     *    - 用于性能监控和分析
     *
     * @param factory Undertow Servlet Web 服务器工厂
     * @see AccessLogHandler 访问日志处理器
     * @see RequestDumpingHandler 请求转储处理器
     * @since 1.0.0
     */
    @Override
    public void customize(@NotNull UndertowServletWebServerFactory factory) {
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });

        // 开启 http2 支持
        if (this.properties.isEnableHttp2()) {
            factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true));
        }

        // 开启 http 请求日志
        factory.addDeploymentInfoCustomizers(
            info -> info.addInitialHandlerChainWrapper(
                next -> new CustomRequestDumpingHandler(next, this.properties)));

        // %D 需要开启 undertow 记时 (access.log)
        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true));
    }

    /**
     * 自定义请求转储处理器
     *
     * 这个内部静态类实现了自定义的 HTTP 请求处理逻辑，主要用于在请求处理过程中
     * 根据配置动态决定是否记录详细的请求日志。它包装了下一个处理器，在处理
     * 请求前进行日志记录判断。
     *
     * 设计特点：
     * - 动态日志开关：每次请求都会检查是否需要记录日志
     * - 配置驱动：通过 enableContainerLog 配置控制日志记录
     * - 透明处理：不影响正常的请求处理流程
     * - 性能优化：只在需要时才进行日志处理
     *
     * 日志记录条件：
     * 1. 日志等级必须设置为 trace 级别
     * 2. 必须配置 enable-container-log: true
     *
     * 这种设计允许在运行时动态控制 Undertow 容器的日志输出，
     * 便于生产环境的问题排查和性能调优。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.04.25 19:59
     * @since 1.0.0
     */
    static class CustomRequestDumpingHandler implements HttpHandler {
        /** Properties */
        private final RestProperties properties;
        /** Next */
        private final HttpHandler next;

        /**
         * 自定义请求转储处理器构造函数
         *
         * 初始化请求转储处理器，保存下一个处理器的引用和配置属性。
         *
         * @param next       下一个 HTTP 处理器
         * @param properties REST 配置属性，包含日志开关等配置
         * @since 1.0.0
         */
        CustomRequestDumpingHandler(HttpHandler next, RestProperties properties) {
            this.next = next;
            this.properties = properties;
        }

        /**
         * 处理 HTTP 请求
         *
         * 每次请求都会判断是否需要输出日志，实现动态开关功能。
         * 这个方法在每个 HTTP 请求到达时被调用，根据配置决定是否记录请求详情。
         *
         * 处理流程：
         * 1. 检查日志配置和级别
         * 2. 如果满足条件，记录请求详细信息
         * 3. 将请求传递给下一个处理器继续处理
         *
         * 日志记录条件：
         * 1. 日志等级必须设置为 trace 级别
         * 2. 必须配置 enable-container-log: true
         *
         * @param exchange HTTP 服务器交换对象，包含请求和响应信息
         * @throws Exception 请求处理过程中可能抛出的异常
         * @since 1.0.0
         */
        @Override
        public void handleRequest(HttpServerExchange exchange) throws Exception {
            ShowUndertowLog.showLog(exchange, this.properties.isEnableContainerLog());
            // Perform the exchange
            this.next.handleRequest(exchange);
        }
    }

}
