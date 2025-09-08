package dev.dong4j.zeka.starter.endpoint.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Endpoint 模块配置属性类
 *
 * 该类定义了 cubo-endpoint-spring-boot 模块的配置属性，
 * 继承自 ZekaProperties 以获得通用的配置管理功能。
 *
 * 配置前缀为 'zeka.endpoint'，用于控制整个 Endpoint 模块的行为，
 * 包括是否启用、预热配置等参数。
 *
 * 通过 @ConfigurationProperties 注解自动绑定配置文件中的相关属性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.28 18:09
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = EndpointProperties.PREFIX)
public class EndpointProperties extends ZekaProperties {
    /** Endpoint 模块配置前缀 */
    public static final String PREFIX = ConfigKey.PREFIX + "endpoint";
}
