package dev.dong4j.zeka.starter.mybatis.autoconfigure;

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
 * @date 2025.06.29 16:49
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
    prefix = MybatisProperties.PREFIX,
    name = ZekaProperties.ENABLED,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true)
@EnableConfigurationProperties(MybatisProperties.class)
public class MybatisAutoConfiguration implements ZekaAutoConfiguration {

    public MybatisAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }
}
