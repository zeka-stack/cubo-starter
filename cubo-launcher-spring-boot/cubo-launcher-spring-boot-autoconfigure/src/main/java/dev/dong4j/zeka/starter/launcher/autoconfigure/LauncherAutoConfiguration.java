package dev.dong4j.zeka.starter.launcher.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.launcher.ZekaStarter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * 启动器自动配置类，负责初始化启动相关的核心组件和配置
 *
 * 该类是启动器模块的核心配置类，具有以下特点：
 * 1. 配置优先级最高，确保在其他自动配置之前加载
 * 2. 封装了启动相关的公共逻辑和初始化过程
 * 3. 依赖于 ZekaStarter 类的存在
 * 4. 启用了 LauncherProperties 配置属性绑定
 *
 * 该配置类会在应用启动时自动加载，并打印日志信息表明已加载。
 *
 * @author dong4j
 * @version 1.0.0
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

    /**
     * 构造方法，初始化启动器自动配置
     * <p>
     * 在配置类被加载时，会打印日志信息表明该自动配置已启用。
     * 这有助于调试和确认配置加载顺序。
     */
    public LauncherAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }
}
