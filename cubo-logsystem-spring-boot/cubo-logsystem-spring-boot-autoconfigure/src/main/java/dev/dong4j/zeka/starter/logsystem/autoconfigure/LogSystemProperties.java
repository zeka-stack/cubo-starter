package dev.dong4j.zeka.starter.logsystem.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.starter.logsystem.Constants;
import dev.dong4j.zeka.starter.logsystem.constant.LogSystem;
import dev.dong4j.zeka.starter.logsystem.entity.LogFile;
import dev.dong4j.zeka.starter.logsystem.entity.Pattern;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.logging.LogLevel;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * 日志系统配置属性类
 *
 * 该类用于绑定和管理日志系统的所有配置属性，支持Spring Boot的配置属性绑定机制。
 * 主要功能包括：
 * 1. 提供日志系统的完整配置属性定义
 * 2. 支持配置属性的类型安全和验证
 * 3. 生成配置元数据，便于IDE提示和配置验证
 * 4. 支持配置属性的动态刷新（通过@RefreshScope）
 *
 * 配置属性包括：
 * - 应用名称配置（用于区分不同应用的日志目录）
 * - 日志配置文件指定（控制台/文件/Docker等不同环境）
 * - 日志级别和分组配置
 * - 日志文件路径、名称、大小等配置
 * - 日志格式模式配置
 * - 日志记录功能开关
 *
 * 使用场景：
 * - Spring Boot应用的日志配置管理
 * - 多环境下的日志配置切换
 * - 配置中心动态配置更新
 * - IDE配置提示和验证
 *
 * 设计意图：
 * 通过统一的配置属性类，提供类型安全、易于使用的日志配置管理，
 * 同时支持配置元数据生成，提升开发体验。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Getter
@Setter
@RefreshScope
@ConfigurationProperties(prefix = LogSystemProperties.PREFIX)
public class LogSystemProperties extends ZekaProperties {

    /** 配置前缀常量 */
    public static final String PREFIX = ConfigKey.PREFIX + "logging";

    /**
     * 应用名称配置
     *
     * 用于区分不同应用的日志目录，默认为应用名。
     * 此配置只对 log4j2-file.xml 有效，最终保存的日志位置为: ${path}/${appName}/${name.log}
     *
     * 使用场景：
     * - 多应用部署时区分日志文件
     * - 微服务架构中的服务日志隔离
     * - 不同环境下的日志分类存储
     */
    private String appName;

    /**
     * 日志配置文件指定
     *
     * 指定使用的日志配置文件名称，支持不同环境的日志配置切换。
     *
     * 配置说明：
     * - 本地开发环境：log4j2-console.xml（日志输出到控制台）
     * - 非本地环境：log4j2-file.xml（日志输出到文件）
     * - 自定义配置：通过 zeka-stack.logging.config = log-config-file-name.xml 指定
     *
     * 默认行为：
     * - 使用 IDE 启动的应用（包括单元测试）为本地开发环境
     * - 文件日志默认输出到 {@link LogSystem#DEFAULT_LOGGING_LOCATION}
     * - 默认日志文件名为 {@link Constants#DEFAULT_FILE_NAME}
     *
     * 注意事项：
     * - 可通过 zeka-stack.logging.file.path 和 zeka-stack.logging.file.name 修改文件路径和名称
     * - 如果默认配置无法满足业务需求，可通过此配置进行覆盖
     */
    private String config;

    /**
     * 是否显示日志位置信息
     *
     * 控制是否在日志输出中包含代码位置信息（类名、方法名、行号等）。
     * 此配置只对 log4j2-console.xml 有效，因为会消耗性能。
     *
     * 使用场景：
     * - 本地开发时快速定位日志输出语句
     * - 调试时快速跳转到源码位置
     * - 提升开发调试效率
     *
     * 注意事项：
     * - 生产环境建议关闭以提升性能
     * - 仅在控制台日志配置中生效
     *
     * @since 1.0.0
     */
    private boolean enableShowLocation;

    /** 日志文件配置 */
    @NestedConfigurationProperty
    private LogFile file = new LogFile();

    /** 日志格式模式配置 */
    @NestedConfigurationProperty
    private Pattern pattern = new Pattern();

    /**
     * 日志等级分组配置
     *
     * 用于将多个Logger组织成逻辑分组，便于统一管理日志级别。
     * 支持将多个包或类名归为一组，然后统一设置该组的日志级别。
     *
     * 配置示例：
     * {@code
     * zeka-stack:
     *   logging:
     *     group:
     *       test: sample.logsystem, sample.launcher
     *       web: org.springframework.web, org.springframework.boot.web
     *     level:
     *       test: info
     *       web: debug
     * }
     *
     * 使用场景：
     * - 按功能模块分组管理日志级别
     * - 简化大量Logger的级别配置
     * - 支持动态调整分组日志级别
     */
    private Map<String, List<String>> group;

    /**
     * 日志等级配置
     *
     * 用于设置特定Logger的日志级别，支持包名、类名等不同粒度的配置。
     * 支持Spring Boot标准的日志级别：TRACE、DEBUG、INFO、WARN、ERROR、OFF
     *
     * 配置示例：
     * {@code
     * zeka-stack:
     *   logging:
     *     level:
     *       sample.logsystem: info
     *       org.springframework: warn
     *       root: info
     * }
     *
     * 使用场景：
     * - 精确控制特定包的日志输出
     * - 开发调试时临时调整日志级别
     * - 生产环境优化日志输出量
     */
    private Map<String, LogLevel> level;

    /** 日志记录功能配置 */
    private Record record = new Record();

    /**
     * 日志记录功能配置类
     * <p>
     * 用于配置日志记录相关的功能开关和参数。
     * 主要控制是否启用日志记录功能，以及相关的记录策略。
     * <p>
     * 使用场景：
     * - 控制是否启用业务日志记录
     * - 动态开关日志记录功能
     * - 配合日志存储服务使用
     */
    @Data
    public static class Record {
        /**
         * 是否启用日志记录功能
         *
         * 控制是否启用业务日志记录功能，包括API日志、操作日志、错误日志等。
         * 当设置为false时，日志记录相关的功能将被禁用。
         *
         * 默认值：false（不启用）
         */
        private boolean enabled;
    }
}
