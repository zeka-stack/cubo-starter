package dev.dong4j.zeka.starter.logsystem.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * Zeka日志级别枚举
 *
 * 定义了Zeka框架中使用的日志级别，用于控制日志输出的详细程度。
 * 该枚举提供了从无日志到完整日志的多个级别，支持不同场景下的日志需求。
 *
 * 主要功能：
 * 1. 提供分层的日志级别定义
 * 2. 支持日志级别的比较和判断
 * 3. 提供详细的日志输出示例
 * 4. 支持不同粒度的日志控制
 *
 * 使用场景：
 * - HTTP请求日志的详细程度控制
 * - 调试和问题排查时的日志输出
 * - 生产环境的日志级别优化
 * - 不同模块的日志级别差异化配置
 *
 * 设计意图：
 * 通过提供清晰的日志级别定义，帮助开发者根据实际需求
 * 选择合适的日志输出级别，平衡调试需求和性能影响。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:19
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum ZekaLogLevel {
    /**
     * 无日志级别
     *
     * 不输出任何日志信息，适用于生产环境或性能敏感场景。
     */
    NONE(0),

    /**
     * 基础日志级别
     *
     * 仅记录请求和响应的基本信息，包括HTTP方法、URL、状态码和响应时间。
     * 适用于生产环境的监控和性能分析。
     *
     * 输出示例：
     * <pre>{@code
     * --> POST /greeting http/1.1 (3-byte body)
     * <-- 200 OK (22ms, 6-byte body)
     * }</pre>
     */
    BASIC(1),

    /**
     * 头部日志级别
     *
     * 记录请求和响应的基本信息以及HTTP头部信息。
     * 适用于调试网络请求和响应头相关问题。
     *
     * 输出示例：
     * <pre>{@code
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * --> END POST
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * <-- END HTTP
     * }</pre>
     */
    HEADERS(2),

    /**
     * 完整日志级别
     *
     * 记录请求和响应的完整信息，包括头部和请求体/响应体内容。
     * 适用于详细的调试和问题排查，但会产生大量日志。
     *
     * 输出示例：
     * <pre>{@code
     * --> POST /greeting http/1.1
     * Host: example.com
     * Content-Type: plain/text
     * Content-Length: 3
     * Hi?
     * --> END POST
     * <-- 200 OK (22ms)
     * Content-Type: plain/text
     * Content-Length: 6
     * Hello!
     * <-- END HTTP
     * }</pre>
     */
    BODY(3);

    /**
     * 日志级别数值
     *
     * 用于日志级别的比较和排序，数值越大表示日志越详细。
     */
    private final int level;

    /**
     * 判断当前日志级别是否小于等于指定级别
     *
     * 用于判断当前日志级别是否满足输出条件。
     * 当日前级别小于等于指定级别时，应该输出日志。
     *
     * 使用场景：
     * - 日志输出前的级别判断
     * - 日志过滤器的级别比较
     * - 动态调整日志输出级别
     *
     * @param level 要比较的日志级别
     * @return true-当前级别小于等于指定级别，false-当前级别大于指定级别
     * @since 1.0.0
     */
    @Contract(pure = true)
    public boolean lte(@NotNull ZekaLogLevel level) {
        return this.level <= level.level;
    }

}
