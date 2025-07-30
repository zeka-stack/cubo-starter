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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * 动态属性日志配置
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.07.30 15:37
 * @since x.x.x
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class RefreshLogLevelAutoConfiguration {

    /**
     * springboot 环境下日志动态刷新
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.07.30
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(LogPrintStream.class)
    @EnableConfigurationProperties(LogSystemProperties.class)
    @ConditionalOnEnabled(value = LogSystemProperties.PREFIX + ".refresh")
    static class SpringBootDynamicChangeLogLevel implements ZekaAutoConfiguration {

        public SpringBootDynamicChangeLogLevel() {
            log.info("启动自动配置: [{}]", this.getClass());
        }

        /**
         * 刷新范围注册表
         *
         * @param context 语境
         * @return 2:构建核心配置类注册与管理中心
         */
        @Bean
        @ConditionalOnMissingBean
        public RefreshScopeRegistry refreshScopeRegistry(ApplicationContext context) {
            return new RefreshScopeRegistry(context);
        }

        /**
         * 配置刷新主逻辑
         *
         * @param environment 环境
         * @param registry    注册表
         * @return 6:根据配置变更项（扁平化key集合）精准刷新受影响的
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
     * springcloud 环境下日志动态刷新(修改 yml 中的日志配置时刷新)
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.07.30
     * @since 1.0.0
     */
    @Configuration(proxyBeanMethods = false)
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
         * @since 1.6.0
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
         * @since 1.6.0
         */
        @Bean
        @ConditionalOnMissingBean(ManualChangeLogLevelEventHandler.class)
        public ManualChangeLogLevelEventHandler manualChangeLogLevelEventHandler() {
            return new ManualChangeLogLevelEventHandler();
        }
    }
}
