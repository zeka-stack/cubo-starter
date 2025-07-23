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
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.28 18:09
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnEnabled(value = EndpointProperties.PREFIX)
@ConditionalOnWebApplication
@AutoConfigureAfter({
    ServletStartInfoAutoConfiguration.class,
    ReactiveStartInfoAutoConfiguration.class
})
@ConditionalOnClass(Endpoint.class)
@EnableConfigurationProperties(EndpointProperties.class)
public class EndpointAutoConfiguration implements ZekaAutoConfiguration {

    public EndpointAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * Preload component
     *
     * @param provider 提供者
     * @return the preload component
     */
    @Bean
    @ConditionalOnMissingBean(PreloadComponent.class)
    public PreloadComponent preloadComponent(ObjectProvider<InitializationService> provider) {
        return new PreloadComponent(provider.getIfAvailable());
    }
}
