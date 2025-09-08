package dev.dong4j.zeka.starter.logsystem.autoconfigure;

import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.logsystem.LogPrintStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 日志系统自动配置类
 *
 * 该类是日志系统的核心自动配置类，负责在Spring Boot应用启动时自动配置日志系统。
 * 主要功能包括：
 * 1. 根据条件自动装配日志系统相关组件
 * 2. 启用日志系统配置属性绑定
 * 3. 提供日志系统的基础配置支持
 *
 * 使用场景：
 * - Spring Boot应用启动时的日志系统初始化
 * - 日志配置属性的自动绑定和验证
 * - 日志系统组件的条件装配
 *
 * 设计意图：
 * 通过Spring Boot的自动配置机制，简化日志系统的配置和使用，
 * 让开发者无需手动配置即可使用完整的日志功能。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(LogPrintStream.class)
@EnableConfigurationProperties(LogSystemProperties.class)
public class LogSystemAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造函数
     * <p>
     * 初始化日志系统自动配置类，记录配置启动信息。
     * 在Spring容器创建该Bean时会自动调用此构造函数。
     */
    public LogSystemAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

}
