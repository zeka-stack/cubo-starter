package dev.dong4j.zeka.starter.launcher.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.starter.launcher.refresh.ConfigDiffer;
import dev.dong4j.zeka.starter.launcher.refresh.ConfigFileWatcherRunner;
import dev.dong4j.zeka.starter.launcher.refresh.DynamicConfigLoader;
import dev.dong4j.zeka.starter.launcher.refresh.RefreshScopeRefresher;
import dev.dong4j.zeka.starter.launcher.refresh.RefreshScopeRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = "org.springframework.cloud.context.config.annotation.RefreshScope") // 不判断实际类来源
@ConditionalOnMissingClass("org.springframework.cloud.context.scope.refresh.ContextRefresher") // Spring Cloud 配置刷新不存在时才生效
@ConditionalOnEnabled(value = LauncherProperties.PREFIX)
@ConditionalOnProperty(name = LauncherProperties.PREFIX + "refreshed", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties
public class RefreshScopeAutoConfiguration {

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
     * 配置不同
     *
     * @return 5:比较新旧配置Map，判断哪些配置项发生了变化，并映射到对应的配置类。
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigDiffer configDiffer() {
        return new ConfigDiffer();
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

    /**
     * 配置文件观察器跑步者
     *
     * @param loader      加载程序
     * @param refresher   复习
     * @param differ      不同
     * @param environment 环境
     * @return 配置变更监听执行器
     */
    @Bean
    public ConfigFileWatcherRunner configFileWatcherRunner(
        DynamicConfigLoader loader,
        RefreshScopeRefresher refresher,
        ConfigDiffer differ,
        Environment environment) {
        return new ConfigFileWatcherRunner(loader, refresher, differ, environment);
    }

}

