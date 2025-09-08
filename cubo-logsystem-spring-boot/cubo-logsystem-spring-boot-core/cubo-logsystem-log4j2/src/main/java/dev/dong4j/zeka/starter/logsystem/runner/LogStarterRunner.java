package dev.dong4j.zeka.starter.logsystem.runner;

import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.logsystem.LogPrintStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 日志启动运行器
 *
 * 该类在Spring Boot应用启动完成后执行，负责初始化日志系统的输出流重定向。
 * 主要功能包括：
 * 1. 在应用启动完成后执行日志系统初始化
 * 2. 根据环境判断是否重定向系统输出流
 * 3. 将System.out和System.err重定向到日志系统
 * 4. 确保所有输出都通过统一的日志系统管理
 *
 * 使用场景：
 * - Spring Boot应用启动时的日志系统初始化
 * - 非本地环境下的输出流重定向
 * - 统一管理应用的所有输出信息
 *
 * 设计意图：
 * 通过CommandLineRunner机制，在应用完全启动后执行日志系统的最终初始化，
 * 确保所有输出都通过日志系统进行统一管理。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:19
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(Integer.MAX_VALUE - 1)
public class LogStarterRunner implements CommandLineRunner {

    /**
     * 执行日志系统初始化
     *
     * 在Spring Boot应用启动完成后执行，根据环境判断是否需要重定向系统输出流。
     * 在非本地环境下，将System.out和System.err重定向到日志系统，确保所有输出
     * 都通过统一的日志系统进行管理。
     *
     * 执行逻辑：
     * 1. 记录日志系统初始化信息
     * 2. 判断当前是否为非本地环境
     * 3. 如果是非本地环境，则重定向系统输出流到日志系统
     *
     * 注意事项：
     * - 仅在非本地环境下执行重定向操作
     * - 本地开发环境保持原有的控制台输出
     * - 重定向后所有System.out和System.err都会通过日志系统输出
     *
     * @param args 命令行参数（未使用）
     * @since 1.0.0
     */
    @Override
    public void run(String... args) {
        log.debug("初始化 System.out 处理器: {}", LogStarterRunner.class.getName());

        // 非本地环境下将System.err和System.out重定向到日志系统
        if (ConfigKit.notLocalLaunch()) {
            log.warn("非本地开发环境, System.out 重定向到 log 输出");
            System.setOut(LogPrintStream.out());
            System.setErr(LogPrintStream.err());
        }
    }
}
