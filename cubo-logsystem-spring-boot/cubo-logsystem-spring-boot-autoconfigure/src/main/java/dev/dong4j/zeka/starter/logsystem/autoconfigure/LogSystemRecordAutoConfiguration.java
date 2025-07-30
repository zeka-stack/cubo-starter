package dev.dong4j.zeka.starter.logsystem.autoconfigure;

import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactory;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.31 11:40
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(LogStorageFactory.class)
public class LogSystemRecordAutoConfiguration implements ZekaAutoConfiguration {

    public LogSystemRecordAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

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
