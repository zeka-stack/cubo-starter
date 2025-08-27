package dev.dong4j.zeka.starter.launcher.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.launcher.ZekaStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * <p>Description: 全局启动组件, 封装启动相关公共逻辑, 配置优先级最高</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:55
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnClass(ZekaStarter.class)
@ConditionalOnEnabled(value = LauncherProperties.PREFIX)
@EnableConfigurationProperties(LauncherProperties.class)
public class LauncherAutoConfiguration implements ZekaAutoConfiguration {

    public LauncherAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }
}
