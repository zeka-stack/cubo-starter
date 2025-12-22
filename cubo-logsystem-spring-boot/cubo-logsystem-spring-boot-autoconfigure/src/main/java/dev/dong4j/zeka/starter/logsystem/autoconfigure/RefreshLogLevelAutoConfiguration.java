package dev.dong4j.zeka.starter.logsystem.autoconfigure;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.io.File;
import java.util.List;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.config.refresh.ConfigChangedHandler;
import dev.dong4j.zeka.kernel.common.config.refresh.ConfigFileWatcherCustomizer;
import dev.dong4j.zeka.kernel.common.config.refresh.ConfigFileWatcherRunner;
import dev.dong4j.zeka.kernel.common.config.refresh.DynamicConfigLoader;
import dev.dong4j.zeka.kernel.common.config.refresh.RefreshScopeRefresher;
import dev.dong4j.zeka.kernel.common.config.refresh.RefreshScopeRegistry;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.FileUtils;
import dev.dong4j.zeka.starter.logsystem.LogPrintStream;
import dev.dong4j.zeka.starter.logsystem.handler.AutoChangeLogLevelEventHandler;
import dev.dong4j.zeka.starter.logsystem.handler.ManualChangeLogLevelEventHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 日志级别动态刷新自动配置类
 * <p> 用于在 Spring Boot 和 Spring Cloud 环境中实现日志配置文件的动态刷新功能, 支持在不重启应用的情况下更新日志级别配置.
 * <p> 该配置类通过监听配置文件变化, 自动触发日志配置的重新加载, 适用于需要实时调整日志输出的场景.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
@AutoConfiguration
public class RefreshLogLevelAutoConfiguration {

    /**
     * Spring Boot 动态更改日志级别自动配置类
     * <p> 该类在满足特定条件时启用动态更改日志级别的功能. 通过监听日志配置文件的变化,
     * 实现日志配置的热加载, 无需重启应用程序即可生效.
     * <p>
     * 主要功能包括:
     * - 自动配置 RefreshScopeRegistry 和 RefreshScopeRefresher, 用于管理刷新范围的注册和刷新操作.
     * - 提供 DynamicConfigLoader, 用于加载动态配置.
     * - 实现 ConfigChangedHandler, 当检测到 Log4j2 配置文件变化时, 重新加载日志配置.
     * - 提供 ConfigFileWatcherRunner 和 ConfigFileWatcherCustomizer, 用于监控和自定义配置文件的变更处理.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.22
     * @since 2.0.0
     */
    @AutoConfiguration
    @ConditionalOnClass(LogPrintStream.class)
    @EnableConfigurationProperties(LogSystemProperties.class)
    @ConditionalOnEnabled(value = LogSystemProperties.PREFIX + ".refresh")
    static class SpringBootDynamicChangeLogLevel implements ZekaAutoConfiguration {

        /**
         * 初始化日志动态刷新配置
         * <p> 在类加载时执行, 用于记录自动配置的启动信息.
         *
         * @since 1.0.0
         */
        SpringBootDynamicChangeLogLevel() {
            log.info("启动自动配置: [{}]", this.getClass());
        }

        /**
         * 创建刷新范围注册表 Bean
         * <p>
         * 用于管理需要动态刷新的 Bean 范围, 支持配置变更时的精准刷新.
         * 该注册表会跟踪所有标记为 @RefreshScope 的 Bean, 并在配置变更时
         * 只刷新受影响的 Bean, 避免全量刷新带来的性能问题.
         *
         * @param context Spring 应用上下文, 用于获取 Bean 定义和实例
         * @return 刷新范围注册表实例
         */
        @Bean
        @ConditionalOnMissingBean
        public RefreshScopeRegistry refreshScopeRegistry(ApplicationContext context) {
            return new RefreshScopeRegistry(context);
        }

        /**
         * 创建配置刷新器 Bean
         * <p> 负责执行配置变更时的刷新逻辑, 根据变更的配置项精准刷新受影响的 Bean.
         * 该刷新器会分析配置变更的扁平化 key 集合, 只刷新真正受影响的 Bean,
         * 避免不必要的全量刷新, 提升刷新性能和稳定性.
         *
         * @param environment 环境配置, 用于获取当前配置信息
         * @param registry    刷新范围注册表, 用于管理需要刷新的 Bean
         * @param loader      动态配置加载器, 用于加载和解析配置变更
         * @return 配置刷新器实例
         */
        @Bean
        @ConditionalOnMissingBean
        public RefreshScopeRefresher refreshScopeRefresher(Environment environment, RefreshScopeRegistry registry,
                                                           DynamicConfigLoader loader) {
            return new RefreshScopeRefresher(environment, registry, loader);
        }

        /**
         * 创建动态配置加载器 Bean
         * <p> 用于加载和解析动态配置信息, 支持在运行时读取和解析配置变更.
         * 该加载器能够监听配置文件的变化, 并将变更内容转换为可使用的配置数据.
         *
         * @param environment 环境配置, 用于获取当前应用的配置信息
         * @return 动态配置加载器实例
         */
        @Bean
        @ConditionalOnMissingBean
        public DynamicConfigLoader dynamicConfigLoader(Environment environment) {
            return new DynamicConfigLoader(environment);
        }

        /**
         * 创建配置更改处理程序, 用于处理 Log4j2 配置文件的变更
         * <p> 当 Log4j2 配置文件发生变更时, 此处理程序会重新加载配置文件, 并更新日志配置.
         *
         * @param logSystemProperties 日志系统属性, 包含配置文件路径等信息
         * @return 配置更改处理程序实例
         */
        @Bean
        @Order(-1000)
        public ConfigChangedHandler log4j2XmlConfigChangedHandler(LogSystemProperties logSystemProperties) {
            return (changedFile, changedKeys, latest) -> {
                if (changedFile.equals(logSystemProperties.getConfig())) {
                    final File configFile = new File(FileUtils.appendPath(ConfigKit.getConfigPath(), changedFile));
                    log.info("[RefreshScopeStarter] Reloading Log4j2 config: {}", configFile.getAbsolutePath());
                    try (LoggerContext initialize = Configurator.initialize(null, configFile.getAbsolutePath())) {
                        final String name = initialize.getName();
                        log.info("重载日志配置: {}", name);
                    }
                }
            };
        }

        /**
         * 配置文件监听器 (yaml 文件)
         * <p> 创建一个配置文件监听器, 用于监听配置文件的变化并执行相应的处理逻辑.
         *
         * @param loader          加载程序, 用于加载和解析配置文件
         * @param handlerProvider 处理程序提供者, 提供配置变更时的处理逻辑
         * @return 配置变更监听执行器
         */
        @Bean
        @ConditionalOnMissingBean
        public ConfigFileWatcherRunner configFileWatcherRunner(
            DynamicConfigLoader loader,
            ObjectProvider<List<ConfigChangedHandler>> handlerProvider) {
            return new ConfigFileWatcherRunner(loader, handlerProvider);
        }

        /**
         * 添加 Spring Boot 应用的日志配置文件监控
         * <p> 配置需要监听的日志文件, 确保在配置文件发生变化时能够触发相应的处理逻辑.
         *
         * @param logSystemProperties 日志系统属性, 包含配置文件路径等信息
         * @return 配置文件监控自定义器, 用于注册需要监听的文件
         * @since 1.0.0
         */
        @Bean
        public ConfigFileWatcherCustomizer log4j2XmlFileWatcher(LogSystemProperties logSystemProperties) {
            return runner -> runner.registerWatchedFile(logSystemProperties.getConfig());
        }

    }

    /**
     * Spring Cloud 动态变更日志级别自动配置类
     * <p> 该类在满足特定条件时自动配置日志级别的动态变更相关事件处理器. 通过监听环境变化事件,
     * 实现日志级别的自动调整. 具体功能包括自动配置日志级别变更事件处理器和手动日志级别变更事件处理器.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.22
     * @since 2.0.0
     */
    @AutoConfiguration
    @ConditionalOnClass(EnvironmentChangeEvent.class)
    @ConditionalOnEnabled(value = LogSystemProperties.PREFIX + ".refresh")
    static class SpringCloudDynamicChangeLogLevel implements ZekaAutoConfiguration {

        /**
         * 构造方法, 用于初始化 SpringCloudDynamicChangeLogLevel 实例
         * <p> 在实例化时记录日志, 表明自动配置已启动
         *
         * @since 1.0.0
         */
        SpringCloudDynamicChangeLogLevel() {
            log.info("启动自动配置: [{}]", this.getClass());
        }

        /**
         * 当环境配置改变时, 自动检查是否需要修改日志等级
         * <p> 该方法用于创建并返回一个自动处理日志等级变更的事件处理器, 用于监听配置中心的环境变更事件, 并根据配置变化动态调整日志级别.
         *
         * @return the logging level rebinder
         * @since 1.0.0
         */
        @Contract(value = " -> new", pure = true)
        @Bean
        @ConditionalOnMissingBean(AutoChangeLogLevelEventHandler.class)
        public static @NotNull AutoChangeLogLevelEventHandler autoChangeLogLevelEventHandler() {
            return new AutoChangeLogLevelEventHandler();
        }

        /**
         * 注册手动日志级别修改事件处理器
         * <p> 当检测到没有手动日志级别修改事件处理器时, 创建并注册一个默认的处理器.
         *
         * @return 手动日志级别修改事件处理器实例
         */
        @Bean
        @ConditionalOnMissingBean(ManualChangeLogLevelEventHandler.class)
        public ManualChangeLogLevelEventHandler manualChangeLogLevelEventHandler() {
            return new ManualChangeLogLevelEventHandler();
        }
    }
}
