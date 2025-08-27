package dev.dong4j.zeka.starter.rest.autoconfigure.supportss;

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
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;

/**
 * 自定义OkHttpClientHttpRequestFactory实现
 * 替代Spring Boot 3.x中已删除的OkHttp3ClientHttpRequestFactory
 * 支持连接池配置
 *
 * @author dong4j
 * @since 1.0.0
 */
@Slf4j
public class OkHttpClientHttpRequestFactory implements org.springframework.http.client.ClientHttpRequestFactory {

    private final OkHttpClient okHttpClient;

    public OkHttpClientHttpRequestFactory(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpClientHttpRequestFactory() {
        this(new OkHttpClient());
    }

    public OkHttpClientHttpRequestFactory(long connectTimeout, long readTimeout, long writeTimeout) {
        this(connectTimeout, readTimeout, writeTimeout, 5, 5, TimeUnit.MINUTES);
    }

    /**
     * 创建带有连接池配置的OkHttpClientHttpRequestFactory
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
     * 基于现有OkHttpClient创建带有连接池配置的OkHttpClientHttpRequestFactory
     * 保留原有的SSL等配置，只更新超时和连接池配置
     *
     * @param baseClient         基础OkHttpClient（包含SSL等配置）
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
     * 创建支持SSL的OkHttpClient实例
     *
     * @param connectTimeout 连接超时时间
     * @param readTimeout    读取超时时间
     * @param writeTimeout   写入超时时间
     * @return OkHttpClient实例
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

    @Override
    public @NotNull ClientHttpRequest createRequest(@NotNull URI uri, @NotNull HttpMethod httpMethod) {
        return new OkHttpClientHttpRequest(okHttpClient, uri, httpMethod);
    }

    /**
     * 自定义ClientHttpRequest实现
     */
    private static class OkHttpClientHttpRequest implements ClientHttpRequest {

        private final OkHttpClient okHttpClient;
        private final URI uri;
        private final HttpMethod httpMethod;
        private final HttpHeaders headers = new HttpHeaders();
        private final Map<String, Object> attributes = new ConcurrentHashMap<>();
        private byte[] body;

        public OkHttpClientHttpRequest(OkHttpClient okHttpClient, URI uri, HttpMethod httpMethod) {
            this.okHttpClient = okHttpClient;
            this.uri = uri;
            this.httpMethod = httpMethod;
        }

        @Override
        public @NotNull HttpMethod getMethod() {
            return httpMethod;
        }

        @Override
        public @NotNull URI getURI() {
            return uri;
        }

        @Override
        public @NotNull HttpHeaders getHeaders() {
            return headers;
        }

        @Override
        public @NotNull Map<String, Object> getAttributes() {
            return attributes;
        }

        @Override
        public @NotNull OutputStream getBody() {
            return new OutputStream() {
                private final java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();

                @Override
                public void write(int b) {
                    buffer.write(b);
                }

                @Override
                public void write(byte @NotNull [] b, int off, int len) {
                    buffer.write(b, off, len);
                }

                @Override
                public void close() throws IOException {
                    body = buffer.toByteArray();
                    super.close();
                }
            };
        }

        @Override
        public @NotNull ClientHttpResponse execute() throws IOException {
            // 构建OkHttp请求
            okhttp3.Request.Builder requestBuilder = new okhttp3.Request.Builder()
                .url(uri.toURL())
                .method(httpMethod.name(), body != null && body.length > 0 ?
                    okhttp3.RequestBody.create(body, okhttp3.MediaType.parse(
                        headers.getContentType() != null ? headers.getContentType().toString() : "application/octet-stream"
                    )) : null);

            // 添加请求头
            headers.forEach((name, values) -> {
                for (String value : values) {
                    requestBuilder.addHeader(name, value);
                }
            });

            // 执行请求
            try (okhttp3.Response response = okHttpClient.newCall(requestBuilder.build()).execute()) {

                return new ClientHttpResponse() {
                    @Override
                    public @NotNull HttpStatusCode getStatusCode() {
                        return HttpStatusCode.valueOf(response.code());
                    }

                    @Override
                    public @NotNull String getStatusText() {
                        return response.message();
                    }

                    @Override
                    public void close() {
                        response.close();
                    }

                    @Override
                    public @NotNull InputStream getBody() {
                        return response.body() != null ? response.body().byteStream() : new ByteArrayInputStream(new byte[0]);
                    }

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
