package dev.dong4j.zeka.starter.logsystem.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactory;
import dev.dong4j.zeka.starter.logsystem.factory.LogStorageFactoryAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * 日志记录自动配置类
 * <p>
 * 该类负责日志记录功能的自动配置，包括日志存储工厂的创建和配置。
 * 主要功能包括：
 * 1. 根据条件自动装配日志记录相关组件
 * 2. 提供默认的日志存储工厂实现
 * 3. 支持日志记录功能的开关控制
 * 4. 为业务端提供日志存储服务接口
 * <p>
 * 使用场景：
 * - 启用日志记录功能时的自动配置
 * - 提供默认的日志存储实现
 * - 业务端自定义日志存储服务
 * <p>
 * 设计意图：
 * 通过自动配置机制，简化日志记录功能的集成和使用，
 * 同时提供灵活的扩展点供业务端自定义实现。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.31 11:40
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(LogStorageFactory.class)
@ConditionalOnEnabled(value = LogSystemProperties.PREFIX + ".record")
public class LogSystemRecordAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造函数
     * <p>
     * 初始化日志记录自动配置类，记录配置启动信息。
     * 在Spring容器创建该Bean时会自动调用此构造函数。
     */
    public LogSystemRecordAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 创建日志存储工厂Bean
     * <p>
     * 当没有自定义的日志存储工厂时，提供默认的空实现。
     * 该实现不会实际存储日志，仅用于避免启动错误。
     * <p>
     * 使用场景：
     * - 业务端未配置自定义日志存储服务时
     * - 作为日志记录功能的默认实现
     * - 避免因缺少Bean而导致的启动失败
     * <p>
     * 注意事项：
     * - 该实现不会实际存储任何日志
     * - 业务端需要配置具体的日志存储服务才能正常记录日志
     * - 建议在生产环境中配置真实的日志存储实现
     *
     * @return 默认的日志存储工厂实现
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
