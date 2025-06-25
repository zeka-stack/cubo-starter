package dev.dong4j.zeka.starter.{{name}}.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date {{date}}
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = {{Name}}Properties.PREFIX,
    name = ZekaProperties.ENABLE,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true)
@EnableConfigurationProperties({{Name}}Properties.class)
public class {{Name}}AutoConfiguration implements ZekaAutoConfiguration {

    public {{Name}}AutoConfiguration() {
        log.info("[{}] start autoconfiguration....", {{Name}}AutoConfiguration.class);
    }
}
