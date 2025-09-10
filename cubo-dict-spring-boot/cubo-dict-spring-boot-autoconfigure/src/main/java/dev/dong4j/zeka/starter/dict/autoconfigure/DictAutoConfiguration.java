package dev.dong4j.zeka.starter.dict.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 22:49
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(
    prefix = DictProperties.PREFIX,
    name = ZekaProperties.ENABLED,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true)
@EnableConfigurationProperties(DictProperties.class)
public class DictAutoConfiguration implements ZekaAutoConfiguration {

    public DictAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }
}
