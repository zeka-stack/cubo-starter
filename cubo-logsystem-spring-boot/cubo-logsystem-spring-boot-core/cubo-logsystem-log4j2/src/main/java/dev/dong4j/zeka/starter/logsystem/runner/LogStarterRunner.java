package dev.dong4j.zeka.starter.logsystem.runner;

import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.logsystem.LogPrintStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:19
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(Integer.MAX_VALUE - 1)
public class LogStarterRunner implements CommandLineRunner {

    /**
     * Run *
     *
     * @param args args
     * @since 1.0.0
     */
    @Override
    public void run(String... args) {
        log.debug("初始化 System.out 处理器: {}", LogStarterRunner.class.getName());

        // 非本地 将 全部的 System.err 和 System.out 替换为log
        if (ConfigKit.notLocalLaunch()) {
            log.warn("非本地开发环境, System.out 重定向到 log 输出");
            System.setOut(LogPrintStream.out());
            System.setErr(LogPrintStream.err());
        }
    }
}
