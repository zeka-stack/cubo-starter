package dev.dong4j.zeka.starter.rest.autoconfigure.supportss;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

/**
 * OkHttp 客户端请求工厂类
 * <p> 该类实现了 Spring 的 ClientHttpRequestFactory 接口, 用于创建和管理 OkHttp 客户端请求. 通过不同的构造函数可以灵活地设置连接超时, 读取超时, 写入超时以及连接池的最大空闲连接数和存活时间.
 * <p> 提供了多种构造函数以满足不同的使用场景, 支持基于现有 OkHttpClient 创建新的客户端实例, 并且可以通过静态方法创建不安全的 OkHttpClient 实例.
 * <p> 内部使用 OkHttp 进行 HTTP 请求的发送和接收, 支持自定义请求头, 请求体以及响应处理.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
public class OkHttpClientHttpRequestFactory implements org.springframework.http.client.ClientHttpRequestFactory {

    /** 用于执行 HTTP 请求的 OkHttpClient 实例 */
    private final OkHttpClient okHttpClient;

    /**
     * 使用指定的 OkHttpClient 创建 OkHttpClientHttpRequestFactory 实例
     *
     * @param okHttpClient 自定义的 OkHttpClient 实例, 包含所需的 SSL, 超时等配置
     */
    public OkHttpClientHttpRequestFactory(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    /**
     * 默认构造函数, 使用默认配置创建 OkHttpClientHttpRequestFactory 实例
     * <p> 内部会创建一个默认的 OkHttpClient 实例, 并配置连接池等基础参数
     *
     * @since 1.0.0
     */
    public OkHttpClientHttpRequestFactory() {
        this(new OkHttpClient());
    }

    /**
     * 设置连接超时, 读取超时和写入超时时间
     * <p> 调用带连接池配置的构造函数, 使用默认的连接池参数 (最大空闲连接数为 5, 连接保活时间为 5 分钟)
     *
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读取超时时间
     * @param writeTimeout   写入超时时间
     */
    public OkHttpClientHttpRequestFactory(long connectTimeout, long readTimeout, long writeTimeout) {
        this(connectTimeout, readTimeout, writeTimeout, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 创建带有连接池配置的 OkHttpClientHttpRequestFactory
     * <p> 根据给定的超时时间和连接池配置创建一个新的 OkHttpClientHttpRequestFactory 实例, 并记录创建信息.
     *
     * @param connectTimeout     连接超时时间
     * @param readTimeout        读取超时时间
     * @param writeTimeout       写入超时时间
     * @param maxIdleConnections 最大空闲连接数
     * @param keepAliveDuration  连接保活时间
     * @param timeUnit           时间单位
     */
    public OkHttpClientHttpRequestFactory(long connectTimeout, long readTimeout, long writeTimeout,
                                          int maxIdleConnections, long keepAliveDuration, TimeUnit timeUnit) {
        // 创建连接池配置
        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, timeUnit);

        this.okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .connectionPool(connectionPool)
            .build();

        log.info("创建OkHttpClient，连接池配置: maxIdleConnections={}, keepAliveDuration={} {}",
                 maxIdleConnections, keepAliveDuration, timeUnit);
    }

    /**
     * 基于现有 OkHttpClient 创建带有连接池配置的 OkHttpClientHttpRequestFactory
     * 保留原有的 SSL 等配置, 只更新超时和连接池配置
     *
     * @param baseClient         基础 OkHttpClient(包含 SSL 等配置)
     * @param connectTimeout     连接超时时间
     * @param readTimeout        读取超时时间
     * @param writeTimeout       写入超时时间
     * @param maxIdleConnections 最大空闲连接数
     * @param keepAliveDuration  连接保活时间
     * @param timeUnit           时间单位
     */
    public OkHttpClientHttpRequestFactory(OkHttpClient baseClient,
                                          long connectTimeout,
                                          long readTimeout,
                                          long writeTimeout,
                                          int maxIdleConnections,
                                          long keepAliveDuration,
                                          TimeUnit timeUnit) {
        // 创建连接池配置
        ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, timeUnit);

        // 基于现有客户端构建新的客户端，保留SSL等配置
        this.okHttpClient = baseClient.newBuilder()
            .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .connectionPool(connectionPool)
            .build();

        log.info("基于现有OkHttpClient创建新客户端，连接池配置: maxIdleConnections={}, keepAliveDuration={} {}",
                 maxIdleConnections, keepAliveDuration, timeUnit);
    }

    /**
     * 创建一个不安全的 OkHttpClient 实例
     * <p> 该方法用于创建一个不进行 SSL 验证的 OkHttpClient 实例. 在捕获异常的情况下, 会记录警告日志并继续使用默认配置创建实例.
     *
     * @param connectTimeout 连接超时时间, 单位为毫秒
     * @param readTimeout    读取超时时间, 单位为毫秒
     * @param writeTimeout   写入超时时间, 单位为毫秒
     * @return 创建的 OkHttpClient 实例
     */
    public static OkHttpClient createUnsafeOkHttpClient(long connectTimeout, long readTimeout, long writeTimeout) {
        try {
            // 这里需要访问RestTemplateAutoConfiguration中的getUnsafeOkHttpClient方法
            // 或者我们可以将SSL配置逻辑移到这里
            return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .build();
        } catch (Exception e) {
            log.warn("创建OkHttpClient失败，使用默认配置", e);
            return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .build();
        }
    }

    /**
     * 创建一个新的 HTTP 请求对象
     * <p> 根据给定的 URI 和 HTTP 方法创建一个 {@link OkHttpClientHttpRequest} 对象
     *
     * @param uri        请求的目标 URI
     * @param httpMethod 请求的 HTTP 方法
     * @return 返回一个新的 {@link ClientHttpRequest} 对象
     */
    @Override
    public @NotNull ClientHttpRequest createRequest(@NotNull URI uri, @NotNull HttpMethod httpMethod) {
        return new OkHttpClientHttpRequest(okHttpClient, uri, httpMethod);
    }

    /**
     * OkHttpClientHttpRequest
     * <p> 基于 OkHttpClient 实现的 HTTP 请求对象, 用于封装和执行 HTTP 请求, 支持设置请求方法,URI, 请求头, 属性以及请求体, 并能够执行请求并返回响应结果.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.22
     * @since 2.0.0
     */
    private static class OkHttpClientHttpRequest implements ClientHttpRequest {

        /** 用于发送 HTTP 请求的 OkHttpClient 实例 */
        private final OkHttpClient okHttpClient;
        /** 请求的 URI 地址 */
        private final URI uri;
        /** HTTP 请求方法 */
        private final HttpMethod httpMethod;
        /** 请求头信息 */
        private final HttpHeaders headers = new HttpHeaders();
        /** 用于存储请求属性的线程安全 Map */
        private final Map<String, Object> attributes = new ConcurrentHashMap<>();
        /** 请求体的字节数组 */
        private byte[] body;

        /**
         * 构造 OkHttpClientHttpRequest 实例
         * <p> 初始化自定义的 ClientHttpRequest 实现, 用于基于 OkHttp 的 HTTP 请求
         *
         * @param okHttpClient OkHttp 客户端实例
         * @param uri          请求的目标 URI
         * @param httpMethod   请求使用的 HTTP 方法
         */
        OkHttpClientHttpRequest(OkHttpClient okHttpClient, URI uri, HttpMethod httpMethod) {
            this.okHttpClient = okHttpClient;
            this.uri = uri;
            this.httpMethod = httpMethod;
        }

        /**
         * 获取当前 HTTP 请求的方法
         * <p> 返回请求所使用的 HTTP 方法 (GET, POST 等)
         *
         * @return 当前 HTTP 请求的方法
         */
        @Override
        public @NotNull HttpMethod getMethod() {
            return httpMethod;
        }

        /**
         * 获取请求的 URI 地址
         * <p> 返回当前请求所对应的 URI 信息
         *
         * @return 请求的 URI 地址
         */
        @Override
        @SuppressWarnings("PMD.LowerCamelCaseVariableNamingRule")
        public @NotNull URI getURI() {
            return uri;
        }

        /**
         * 获取当前请求的 HTTP 头信息
         * <p> 返回封装的 HTTP 头对象, 包含所有已设置的请求头信息
         *
         * @return 请求的 HTTP 头信息
         */
        @Override
        public @NotNull HttpHeaders getHeaders() {
            return headers;
        }

        /**
         * 获取请求属性映射
         * <p> 返回与当前请求关联的属性映射, 用于存储自定义数据或元信息
         *
         * @return 请求属性映射, 键为字符串, 值为对象
         */
        @Override
        public @NotNull Map<String, Object> getAttributes() {
            return attributes;
        }

        /**
         * 获取用于写入请求体的输出流
         * <p> 返回一个输出流, 用于将数据写入请求体. 当流关闭时, 数据会被写入到内部缓冲区并存储在 `body` 字段中.
         *
         * @return 用于写入请求体的输出流
         */
        @Override
        public @NotNull OutputStream getBody() {
            return new OutputStream() {
                /** 用于存储字节数据的缓冲流 */
                private final java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();

                /**
                 * 将指定的字节写入输出流缓冲区
                 * <p> 该方法将单个字节写入内部的 ByteArrayOutputStream 对象中
                 *
                 * @param b 要写入的字节
                 */
                @Override
                public void write(int b) {
                    buffer.write(b);
                }

                /**
                 * 将指定字节数组中的数据写入缓冲区
                 * <p> 从给定的字节数组中, 从偏移量 off 开始, 写入 len 个字节到缓冲区
                 *
                 * @param b   字节数组
                 * @param off 偏移量
                 * @param len 要写入的字节数
                 */
                @Override
                public void write(byte @NotNull [] b, int off, int len) {
                    buffer.write(b, off, len);
                }

                /**
                 * 关闭输出流并处理缓冲数据
                 * <p> 将缓冲区中的数据转换为字节数组并存储到 body 变量中, 然后调用父类的 close 方法完成资源释放.
                 *
                 * @throws IOException 如果在关闭过程中发生 I/O 错误
                 */
                @Override
                public void close() throws IOException {
                    body = buffer.toByteArray();
                    super.close();
                }
            };
        }

        /**
         * 执行 HTTP 请求并返回响应
         * <p> 使用配置的 OkHttpClient 发起 HTTP 请求, 并将 OkHttp 的响应封装为 ClientHttpResponse 返回.
         * 该方法会处理请求头, 请求体以及响应的转换.
         *
         * @return 封装了 OkHttp 响应的 ClientHttpResponse 对象
         * @throws IOException 如果请求过程中发生 I/O 错误
         */
        @Override
        public @NotNull ClientHttpResponse execute() throws IOException {
            // 构建OkHttp请求
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .url(uri.toURL())
                .method(httpMethod.name(),
                        body != null && body.length > 0
                        ? okhttp3.RequestBody.create(body, okhttp3.MediaType.parse(
                            headers.getContentType() != null
                            ? headers.getContentType().toString()
                            : "application/octet-stream"))
                        : null);

            // 添加请求头
            headers.forEach((name, values) -> {
                for (String value : values) {
                    requestBuilder.addHeader(name, value);
                }
            });

            // 执行请求
            try (okhttp3.Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {

                return new ClientHttpResponse() {
                    /**
                     * 获取 HTTP 响应的状态码
                     * <p> 返回当前 HTTP 响应对应的状态码
                     *
                     * @return HTTP 状态码
                     */
                    @Override
                    public @NotNull HttpStatusCode getStatusCode() {
                        return HttpStatusCode.valueOf(response.code());
                    }

                    /**
                     * 获取 HTTP 响应的状态文本
                     * <p> 返回 HTTP 响应的状态消息
                     *
                     * @return HTTP 响应的状态文本
                     */
                    @Override
                    public @NotNull String getStatusText() {
                        return response.message();
                    }

                    /**
                     * 关闭 HTTP 响应流, 释放相关资源
                     * <p> 调用底层响应对象的 close 方法以关闭响应流并释放相关资源
                     *
                     */
                    @Override
                    public void close() {
                        response.close();
                    }

                    /**
                     * 获取响应体的输入流
                     * <p> 如果响应体存在, 则返回其字节流; 否则返回一个空的字节数组输入流
                     *
                     * @return 响应体的输入流
                     */
                    @Override
                    public @NotNull InputStream getBody() {
                        return response.body() != null ? response.body().byteStream() : new ByteArrayInputStream(new byte[0]);
                    }

                    /**
                     * 获取响应头信息
                     * <p> 将原始响应中的头信息转换为 HttpHeaders 对象并返回.
                     *
                     * @return 响应头信息, 包含所有原始头字段
                     */
                    @Override
                    public @NotNull HttpHeaders getHeaders() {
                        HttpHeaders responseHeaders = new HttpHeaders();
                        response.headers().forEach(header ->
                                                       responseHeaders.add(header.getFirst(), header.getSecond()));
                        return responseHeaders;
                    }
                };
            }
        }
    }
}
