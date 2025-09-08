package dev.dong4j.zeka.starter.launcher.runner;

import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.context.SpringContext;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.StartUtils;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 启动信息输出运行器，在应用启动完成后输出相关信息
 *
 * 该类实现了 ApplicationRunner 接口，会在应用启动完成后执行，
 * 主要功能包括：
 * 1. 显示调试信息（配置信息和 Spring 上下文信息）
 * 2. 根据启动类型决定是否输出启动信息
 * 3. 输出库组件信息和简单启动信息
 *
 * 使用场景：
 * 1. 开发环境调试
 * 2. 生产环境启动日志
 * 3. 单元测试环境
 *
 * 注意：在单元测试环境下会跳过信息输出
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 18:09
 * @since 1.0.0
 */
@Slf4j
@Order
@Component
public class StarterInfoRunner implements ApplicationRunner {

    /**
     * 运行方法，在应用启动完成后执行
     *
     * @param args 应用启动参数
     * @since 1.0.0
     */
    @Override
    public void run(ApplicationArguments args) {
        try {
            ConfigKit.showDebugInfo();
            SpringContext.showDebugInfo();
        } catch (IllegalStateException e) {
            log.warn("{}", e.getMessage());
        }

        if (App.START_JUNIT.equals(ConfigKit.getProperty(App.START_TYPE))) {
            return;
        }

        String str = MDC.get(App.LIBRARY_NAME);
        if (StringUtils.isNotBlank(str)) {
            String[] components = str.split(StringPool.AT);
            StartUtils.printStartedInfo(components);
        } else {
            StartUtils.printSimpleInfo();
        }
        MDC.remove(App.LIBRARY_NAME);
    }
}
