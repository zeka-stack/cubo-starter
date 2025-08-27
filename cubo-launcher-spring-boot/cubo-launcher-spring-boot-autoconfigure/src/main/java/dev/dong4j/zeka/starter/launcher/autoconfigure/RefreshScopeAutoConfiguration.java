package dev.dong4j.zeka.starter.launcher.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.config.refresh.ConfigChangedHandler;
import dev.dong4j.zeka.kernel.common.config.refresh.ConfigFileWatcherCustomizer;
import dev.dong4j.zeka.kernel.common.config.refresh.ConfigFileWatcherRunner;
import dev.dong4j.zeka.kernel.common.config.refresh.DynamicConfigLoader;
import dev.dong4j.zeka.kernel.common.config.refresh.RefreshScopeRefresher;
import dev.dong4j.zeka.kernel.common.config.refresh.RefreshScopeRegistry;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.support.StrFormatter;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

/**
 * 配置文件自动刷新
 * 1. 自动配置类 RefreshScopeAutoConfiguration（自动注入各组件）
 * 2. 条件注解判断是否存在 Spring Cloud 的原生 @RefreshScope
 * 3. 自动运行监听器线程启动 ConfigFileWatcherRunner
 * 4. Starter 自动注册文件路径说明（支持 Spring Boot 3.x 的 AutoConfiguration.imports）
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.07.29
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(name = "org.springframework.cloud.context.config.annotation.RefreshScope") // 不判断实际类来源
@ConditionalOnMissingClass("org.springframework.cloud.context.scope.refresh.ContextRefresher") // Spring Cloud 配置刷新不存在时才生效
@ConditionalOnEnabled(value = LauncherProperties.PREFIX)
@ConditionalOnProperty(name = LauncherProperties.PREFIX + "refresh", havingValue = "true", matchIfMissing = true)
public class RefreshScopeAutoConfiguration implements ZekaAutoConfiguration {

    public RefreshScopeAutoConfiguration() {
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
     * 动态配置加载程序
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
     * 刷新范围更新
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

    @Bean
    @Order(0)
    public ConfigChangedHandler yamlConfigChangedHandler(RefreshScopeRefresher refresher) {
        return (changedFile, changedKeys, latest) -> {
            if (changedFile.startsWith(ConfigKit.BOOT_CONFIG_FILE_PREFIX)) {
                refresher.refreshByChangedKeys(changedKeys);
            }
        };
    }

    /**
     * 配置文件观察器跑步者
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
     * 添加 springboot 应用配置文件监控
     * 1. application.yml
     * 2. application-{env}.yml
     *
     * @param environment 环境
     * @return 自定义需要监听的文件
     */
    @Bean
    public ConfigFileWatcherCustomizer applicationConfigFileWatcher(Environment environment) {
        return runner -> {
            Set<String> watched = new HashSet<>();
            watched.add(ConfigKit.BOOT_CONFIG_FILE_NAME);
            for (String profile : environment.getActiveProfiles()) {
                String configFile = StrFormatter.format(ConfigKit.BOOT_ENV_CONFIG_FILE_NAME, profile);
                watched.add(configFile);
            }
            watched.forEach(runner::registerWatchedFile);
        };
    }

}

