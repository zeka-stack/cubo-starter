package dev.dong4j.zeka.starter.logsystem.service;

import dev.dong4j.zeka.starter.logsystem.publisher.BusinessLogPublisher;
import org.jetbrains.annotations.Contract;

/**
 * 业务日志发送服务
 *
 * 该类提供业务日志发送的统一接口，支持不同级别的日志记录。
 * 通过BusinessLogPublisher异步发送业务日志，便于业务操作的追踪和分析。
 *
 * 主要功能包括：
 * 1. 提供不同级别的日志发送方法（info、debug、warn、error）
 * 2. 支持业务日志的异步发送
 * 3. 提供统一的日志发送接口
 * 4. 简化业务日志的记录操作
 *
 * 使用场景：
 * - 业务操作的日志记录
 * - 业务流程的追踪和分析
 * - 业务数据的审计和监控
 * - 业务异常的记录和告警
 *
 * 设计意图：
 * 通过统一的服务接口简化业务日志的记录，提供标准化的日志发送能力，
 * 支持业务操作的完整追踪和分析。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.22 16:29
 * @since 1.0.0
 */
public class LoggerService {

    /**
     * 发送信息级别业务日志
     *
     * 发送一般信息级别的业务日志，用于记录正常的业务操作信息。
     * 通过BusinessLogPublisher异步发送日志事件。
     *
     * @param id   日志标识符，用于标识具体的业务操作
     * @param data 日志数据内容
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void info(String id, String data) {
        BusinessLogPublisher.publishEvent("info", id, data);
    }

    /**
     * 发送调试级别业务日志
     *
     * 发送调试级别的业务日志，用于记录详细的调试信息。
     * 通过BusinessLogPublisher异步发送日志事件。
     *
     * @param id   日志标识符，用于标识具体的业务操作
     * @param data 日志数据内容
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void debug(String id, String data) {
        BusinessLogPublisher.publishEvent("debug", id, data);
    }

    /**
     * 发送警告级别业务日志
     *
     * 发送警告级别的业务日志，用于记录需要关注的业务异常情况。
     * 通过BusinessLogPublisher异步发送日志事件。
     *
     * @param id   日志标识符，用于标识具体的业务操作
     * @param data 日志数据内容
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void warn(String id, String data) {
        BusinessLogPublisher.publishEvent("warn", id, data);
    }

    /**
     * 发送错误级别业务日志
     *
     * 发送错误级别的业务日志，用于记录业务处理过程中的错误信息。
     * 通过BusinessLogPublisher异步发送日志事件。
     *
     * @param id   日志标识符，用于标识具体的业务操作
     * @param data 日志数据内容
     * @since 1.0.0
     */
    @Contract(pure = true)
    public static void error(String id, String data) {
        BusinessLogPublisher.publishEvent("error", id, data);
    }
}
