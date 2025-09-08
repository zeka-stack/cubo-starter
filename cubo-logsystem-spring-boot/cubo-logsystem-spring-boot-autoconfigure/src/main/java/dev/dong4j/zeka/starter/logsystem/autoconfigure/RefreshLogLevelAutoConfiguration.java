package dev.dong4j.zeka.starter.logsystem.autoconfigure;

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
import java.io.File;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

/**
 * 动态日志级别刷新自动配置类
 *
 * 该类提供日志级别的动态刷新功能，支持在运行时修改日志级别而无需重启应用。
 * 主要功能包括：
 * 1. 支持Spring Boot环境下的日志级别动态刷新
 * 2. 支持Spring Cloud环境下的配置中心动态刷新
 * 3. 提供配置文件监听和自动重载功能
 * 4. 支持手动和自动两种日志级别修改方式
 *
 * 使用场景：
 * - 生产环境动态调整日志级别进行问题排查
 * - 开发环境快速切换日志输出级别
 * - 配置中心统一管理日志级别配置
 * - 微服务架构下的日志级别集中控制
 *
 * 设计意图：
 * 通过提供灵活的日志级别动态调整机制，提升运维效率和问题排查能力，
 * 同时支持多种环境下的不同刷新策略。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.07.30 15:37
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
public class RefreshLogLevelAutoConfiguration {

    /**
     * Spring Boot环境下的日志动态刷新配置
     *
     * 该类专门处理Spring Boot环境下的日志级别动态刷新功能。
     * 主要功能包括：
     * 1. 配置文件监听和自动重载
     * 2. 刷新范围注册和管理
     * 3. 配置变更处理器注册
     * 4. 日志配置文件监控
     *
     * 使用场景：
     * - 本地开发环境的日志级别动态调整
     * - 配置文件修改后的自动重载
     * - 无需重启应用即可调整日志输出
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.07.30
     * @since 1.0.0
     */
    @AutoConfiguration
    @ConditionalOnClass(LogPrintStream.class)
    @EnableConfigurationProperties(LogSystemProperties.class)
    @ConditionalOnEnabled(value = LogSystemProperties.PREFIX + ".refresh")
    static class SpringBootDynamicChangeLogLevel implements ZekaAutoConfiguration {

        public SpringBootDynamicChangeLogLevel() {
            log.info("启动自动配置: [{}]", this.getClass());
        }

        /**
         * 创建刷新范围注册表Bean
         *
         * 用于管理需要动态刷新的Bean范围，支持配置变更时的精准刷新。
         * 该注册表会跟踪所有标记为@RefreshScope的Bean，并在配置变更时
         * 只刷新受影响的Bean，避免全量刷新带来的性能问题。
         *
         * @param context Spring应用上下文，用于获取Bean定义和实例
         * @return 刷新范围注册表实例
         */
        @Bean
        @ConditionalOnMissingBean
        public RefreshScopeRegistry refreshScopeRegistry(ApplicationContext context) {
            return new RefreshScopeRegistry(context);
        }

        /**
         * 创建配置刷新器Bean
         *
         * 负责执行配置变更时的刷新逻辑，根据变更的配置项精准刷新受影响的Bean。
         * 该刷新器会分析配置变更的扁平化key集合，只刷新真正受影响的Bean，
         * 避免不必要的全量刷新，提升刷新性能和稳定性。
         *
         * @param environment 环境配置，用于获取当前配置信息
         * @param registry 刷新范围注册表，用于管理需要刷新的Bean
         * @param loader 动态配置加载器，用于加载和解析配置变更
         * @return 配置刷新器实例
         */
        @Bean
        @ConditionalOnMissingBean
        public RefreshScopeRefresher refreshScopeRefresher(Environment environment, RefreshScopeRegistry registry, DynamicConfigLoader loader) {
            return new RefreshScopeRefresher(environment, registry, loader);
        }

        /**
         * 配置加载器(yaml)
         *
         * @param environment 环境
         * @return 3:配置加载器
         */
        @Bean
        @ConditionalOnMissingBean
        public DynamicConfigLoader dynamicConfigLoader(Environment environment) {
            return new DynamicConfigLoader(environment);
        }

        /**
         * 配置更改处理程序, 最高优先级
         *
         * @param logSystemProperties 日志系统属性
         * @return 自定义文件变动处理逻辑
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
         * 配置文件监听器(yaml 文件)
         *
         * @param loader 加载程序
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
         * 添加 springboot 应用日志配置文件监控
         * 1. 配置的日志文件名
         * todo-dong4j : (2025.07.30 20:07) [这个文件在 jar 包中, 无法监测文件变动]
         *
         * @return 自定义需要监听的文件
         */
        @Bean
        public ConfigFileWatcherCustomizer log4j2XmlFileWatcher(LogSystemProperties logSystemProperties) {
            return runner -> runner.registerWatchedFile(logSystemProperties.getConfig());
        }

    }

    /**
     * Spring Cloud环境下的日志动态刷新配置
     *
     * 该类专门处理Spring Cloud环境下的日志级别动态刷新功能。
     * 主要功能包括：
     * 1. 监听配置中心的环境变更事件
     * 2. 自动检测日志配置变更并应用
     * 3. 支持手动触发日志级别修改
     * 4. 提供事件驱动的日志级别更新机制
     *
     * 使用场景：
     * - 微服务架构下的配置中心统一管理
     * - 生产环境的日志级别集中控制
     * - 配置变更的实时生效
     * - 运维人员的手动日志级别调整
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.07.30
     * @since 1.0.0
     */
    @AutoConfiguration
    @ConditionalOnClass(EnvironmentChangeEvent.class)
    @ConditionalOnEnabled(value = LogSystemProperties.PREFIX + ".refresh")
    static class SpringCloudDynamicChangeLogLevel implements ZekaAutoConfiguration {

        public SpringCloudDynamicChangeLogLevel() {
            log.info("启动自动配置: [{}]", this.getClass());
        }

        /**
         * 当环境配置改变时 自动检查是否需要修改日志等级
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
         * 监听 ChangeLogLevelEvent 以动态修改日志等级 (手动修改事件)
         *
         * @return the logging level refresh event handler
         * @since 1.0.0
         */
        @Bean
        @ConditionalOnMissingBean(ManualChangeLogLevelEventHandler.class)
        public ManualChangeLogLevelEventHandler manualChangeLogLevelEventHandler() {
            return new ManualChangeLogLevelEventHandler();
        }
    }
}
