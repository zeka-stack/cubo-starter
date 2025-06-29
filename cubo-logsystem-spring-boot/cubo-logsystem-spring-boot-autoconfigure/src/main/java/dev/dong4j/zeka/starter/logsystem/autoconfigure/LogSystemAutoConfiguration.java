package dev.dong4j.zeka.starter.logsystem.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.logsystem.LogPrintStream;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactory;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactoryAdapter;
import dev.dong4j.zeka.starter.logsystem.handler.AutoChangeLogLevelEventHandler;
import dev.dong4j.zeka.starter.logsystem.handler.ManualChangeLogLevelEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(LogPrintStream.class)
@ConditionalOnProperty(
    prefix = LogSystemProperties.PREFIX,
    name = ZekaProperties.ENABLED,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true
)
@EnableConfigurationProperties(LogSystemProperties.class)
public class LogSystemAutoConfiguration implements ZekaAutoConfiguration {

    public LogSystemAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 当环境配置改变时 自动检查是否需要修改日志等级
     *
     * @return the logging level rebinder
     * @since 1.6.0
     */
    @Contract(value = " -> new", pure = true)
    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.context.environment.EnvironmentChangeEvent")
    public static @NotNull AutoChangeLogLevelEventHandler autoChangeLogLevelEventHandler() {
        return new AutoChangeLogLevelEventHandler();
    }

    /**
     * 监听 ChangeLogLevelEvent 以动态修改日志等级 (手动修改事件)
     *
     * @return the logging level refresh event handler
     * @since 1.6.0
     */
    @Bean
    public ManualChangeLogLevelEventHandler manualChangeLogLevelEventHandler() {
        return new ManualChangeLogLevelEventHandler();
    }

    /**
     * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.03.31 11:40
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(name = "dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactory")
    static class LogSystemRecordAutoConfiguration implements ZekaAutoConfiguration {
        /**
         * Log storage factory log storage factory
         *
         * @return the log storage factory
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean
        public LogStorageFactory logStorageFactory() {
            log.warn("未配置任何日志服务, 将不会发送日志, 请在业务端配置.");
            return new LogStorageFactoryAdapter() {
            };
        }
    }
}
