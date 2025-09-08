package dev.dong4j.zeka.starter.rest.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * REST 模块配置属性类
 *
 * 该类定义了 cubo-rest-spring-boot 模块的所有配置属性，
 * 包括 HTTP 客户端配置、连接池配置、JSON 序列化配置、文件上传配置等。
 *
 * 主要配置项：
 * 1. HTTP 请求超时设置（读取、写入、连接超时）
 * 2. 连接池配置（最大空闲连接数、保活时间）
 * 3. Web 服务器功能开关（浏览器自动打开、HTTP2 支持等）
 * 4. 中间件功能开关（缓存过滤器、异常过滤器等）
 * 5. JSON 序列化配置（时间格式、时区等）
 * 6. 文件上传配置（临时目录等）
 *
 * 支持配置动态刷新，使用 @RefreshScope 注解。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = RestProperties.PREFIX)
public class RestProperties extends ZekaProperties {
    /** REST 模块配置前缀 */
    public static final String PREFIX = ConfigKey.PREFIX + "rest";

    /** HTTP 读取超时时间（毫秒） */
    private Integer readTimeout = 5000;
    /** HTTP 写入超时时间（毫秒） */
    private Integer writeTimeout = 5000;
    /** HTTP 连接超时时间（毫秒） */
    private Integer connectTimeout = 3000;
    /** HTTP 连接池配置 */
    private ConnectionPool connectionPool = new ConnectionPool();
    /** 启动完成后是否自动打开浏览器 */
    private boolean enableBrowser = Boolean.FALSE;
    /** 是否开启 Undertow 容器的请求日志 */
    private boolean enableContainerLog = Boolean.FALSE;
    /** 是否开启 HTTP/2 支持 */
    private boolean enableHttp2 = Boolean.FALSE;
    /** 是否开启全局缓存过滤器（request 和 response 缓存） */
    private boolean enableGlobalCacheFilter = Boolean.TRUE;
    /** 是否开启全局异常过滤器 */
    private boolean enableExceptionFilter = Boolean.TRUE;
    /** 是否开启全局参数注入过滤器（token 参数注入处理器） */
    private boolean enableGlobalParameterFilter = Boolean.FALSE;
    /** 是否开启枚举类型全字段序列化 */
    private boolean enableEntityEnumAllFieldSerialize = Boolean.TRUE;
    /** 文件上传相关配置 */
    private Multipart multipart = new Multipart();
    /** JSON 序列化相关配置 */
    private Json json = new Json();

    /**
     * HTTP 连接池配置类
     *
     * 该内部类用于配置 HTTP 客户端的连接池参数，
     * 主要用于优化 HTTP 请求的性能和资源管理。
     *
     * 配置项说明：
     * - maxIdleConnections：最大空闲连接数，用于连接复用
     * - keepAliveDuration：连接保活时间，超过此时间的空闲连接将被关闭
     *
     * 这些配置对于高并发场景下的 HTTP 请求性能至关重要。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.01.01 00:00
     * @since 1.0.0
     */
    @Data
    public static class ConnectionPool {
        /** 最大空闲连接数 */
        private Integer maxIdleConnections = 5;
        /** 连接保活时间（分钟） */
        private Integer keepAliveDuration = 5;
    }

    /**
     * JSON 序列化和反序列化配置类
     *
     * 该内部类用于配置 Jackson 的序列化和反序列化行为，
     * 主要用于统一 API 响应的 JSON 格式。
     *
     * 配置项说明：
     * - dateFormat：时间字段的序列化格式
     * - timeZone：时区设置，影响时间序列化结果
     * - defaultPropertyInclusion：控制空值字段是否参与序列化
     *
     * 这些配置确保了前后端交互中时间格式的一致性。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.02.04 15:44
     * @since 1.0.0
     */
    @Data
    public static class Json {
        /** 设置 jackson 时间序列化格式 */
        private String dateFormat = ConfigDefaultValue.DEFAULT_DATE_FORMAT;
        /** 设置时区 */
        private String timeZone = ConfigDefaultValue.DEFAULT_TIME_ZONE;
        /** 字段为空时不参与序列化 */
        private String defaultPropertyInclusion = ConfigDefaultValue.DEFAULT_PROPERTY_INCLUSION_VALUE;
    }

    /**
     * 文件上传配置类
     *
     * 该内部类用于配置文件上传相关的参数，
     * 主要用于设置 Undertow 容器处理 multipart 请求时的临时文件存储位置。
     *
     * 配置项说明：
     * - location：文件上传时的临时存储目录，默认使用系统临时目录
     *
     * 合理的临时目录配置可以避免磁盘空间不足和权限问题。
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2021.07.13 17:32
     * @since 1.0.0
     */
    @Data
    public static class Multipart {
        /** Undertow 容器目录 */
        private String location = System.getProperty("java.io.tmpdir", ConfigDefaultValue.CONTAINER_LOCATION);
    }
}
