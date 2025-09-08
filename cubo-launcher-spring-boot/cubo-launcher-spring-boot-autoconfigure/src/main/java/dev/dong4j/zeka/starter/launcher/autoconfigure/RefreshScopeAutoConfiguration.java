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
 * 配置自动刷新自动配置类，实现不依赖 Spring Cloud 的配置热更新功能
 *
 * 该类实现了以下核心功能：
 * 1. 自动配置所有必要的组件（注册表、加载器、刷新器等）
 * 2. 检测并避免与 Spring Cloud 的原生刷新机制冲突
 * 3. 启动配置文件监听线程，监控配置变更
 * 4. 支持 Spring Boot 3.x 的 AutoConfiguration.imports 机制
 *
 * 工作流程：
 * 1. 通过 ConfigFileWatcherRunner 监控配置文件变更
 * 2. 使用 DynamicConfigLoader 加载最新配置
 * 3. 通过 RefreshScopeRefresher 精准刷新受影响的 Bean
 *
 * 注意：当 Spring Cloud 的 ContextRefresher 存在时，此自动配置不会生效。
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

    /**
     * 构造方法，初始化配置自动刷新功能
     * <p>
     * 在配置类被加载时，会打印日志信息表明该自动配置已启用。
     * 这有助于调试和确认配置加载顺序。
     */
    public RefreshScopeAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 创建刷新范围注册表 Bean
     *
     * 该注册表用于管理所有需要刷新的 Bean 定义，
     * 当配置变更时，可以精准定位需要刷新的 Bean。
     *
     * @param context Spring 应用上下文
     * @return 刷新范围注册表实例
     */
    @Bean
    @ConditionalOnMissingBean
    public RefreshScopeRegistry refreshScopeRegistry(ApplicationContext context) {
        return new RefreshScopeRegistry(context);
    }

    /**
     * 创建动态配置加载器 Bean
     *
     * 该加载器负责从配置文件中读取最新配置，
     * 并支持多种配置格式（如 YAML、Properties 等）。
     *
     * @param environment Spring 环境对象
     * @return 动态配置加载器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public DynamicConfigLoader dynamicConfigLoader(Environment environment) {
        return new DynamicConfigLoader(environment);
    }

    /**
     * 创建刷新范围更新器 Bean
     *
     * 该更新器负责根据配置变更项（扁平化 key 集合）
     * 精准刷新受影响的 Bean，避免不必要的全量刷新。
     *
     * @param environment Spring 环境对象
     * @param registry 刷新范围注册表
     * @param loader 动态配置加载器
     * @return 刷新范围更新器实例
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
     * 创建配置文件监听器运行器 Bean
     *
     * 该运行器负责启动后台线程，监控配置文件的变更，
     * 并在文件变更时触发相应的处理逻辑。
     *
     * @param loader 动态配置加载器
     * @param handlerProvider 配置变更处理器提供者
     * @return 配置文件监听器运行器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ConfigFileWatcherRunner configFileWatcherRunner(
        DynamicConfigLoader loader,
        ObjectProvider<List<ConfigChangedHandler>> handlerProvider) {
        return new ConfigFileWatcherRunner(loader, handlerProvider);
    }

    /**
     * 创建 Spring Boot 应用配置文件监听器定制器
     *
     * 该定制器负责注册需要监控的配置文件，包括：
     * 1. application.yml
     * 2. application-{env}.yml（根据当前激活的环境）
     *
     * @param environment Spring 环境对象
     * @return 配置文件监听器定制器实例
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

