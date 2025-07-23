package dev.dong4j.zeka.starter.openapi.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.openapi.autoconfigure.knife4j.Knife4jAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:54
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnEnabled(prefix = OpenAPIProperties.PREFIX)
@AutoConfigureAfter({
    Knife4jAutoConfiguration.class,
})
@EnableConfigurationProperties(OpenAPIProperties.class)
public class OpenAPIAutoConfiguration implements ZekaAutoConfiguration {

    public OpenAPIAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }
}
