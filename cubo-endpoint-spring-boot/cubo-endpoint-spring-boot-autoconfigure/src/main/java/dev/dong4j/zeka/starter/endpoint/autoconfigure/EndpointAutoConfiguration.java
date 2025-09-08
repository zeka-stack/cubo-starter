package dev.dong4j.zeka.starter.endpoint.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.endpoint.autoconfigure.reactive.ReactiveStartInfoAutoConfiguration;
import dev.dong4j.zeka.starter.endpoint.autoconfigure.servlet.ServletStartInfoAutoConfiguration;
import dev.dong4j.zeka.starter.endpoint.constant.Endpoint;
import dev.dong4j.zeka.starter.endpoint.initialization.InitializationService;
import dev.dong4j.zeka.starter.endpoint.initialization.PreloadComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Endpoint 模块主自动配置类
 *
 * 该类为 cubo-endpoint-spring-boot 模块提供主要的自动配置功能，
 * 负责根据不同的 Web 环境（Servlet 或 Reactive）加载相应的组件。
 *
 * 主要功能：
 * 1. 根据配置属性控制是否启用 Endpoint 功能
 * 2. 仅在 Web 环境下生效
 * 3. 在 Servlet 和 Reactive 配置后加载
 * 4. 创建预热组件用于应用启动时的性能优化
 *
 * 使用多个条件注解确保在适当的环境下才会生效。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.28 18:09
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnEnabled(value = EndpointProperties.PREFIX)
@ConditionalOnWebApplication
@AutoConfigureAfter({
    ServletStartInfoAutoConfiguration.class,
    ReactiveStartInfoAutoConfiguration.class
})
@ConditionalOnClass(Endpoint.class)
@EnableConfigurationProperties(EndpointProperties.class)
public class EndpointAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造方法
     * <p>
     * 输出启动日志，标识该自动配置类已被加载。
     */
    public EndpointAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 创建预热组件
     *
     * 创建 PreloadComponent Bean，用于在应用启动完成后执行预热操作。
     * 使用 ObjectProvider 以支持可选的依赖注入，如果没有对应的
     * InitializationService 实现则传入 null。
     *
     * @param provider InitializationService 的可选提供者
     * @return PreloadComponent 实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(PreloadComponent.class)
    public PreloadComponent preloadComponent(ObjectProvider<InitializationService> provider) {
        return new PreloadComponent(provider.getIfAvailable());
    }
}
