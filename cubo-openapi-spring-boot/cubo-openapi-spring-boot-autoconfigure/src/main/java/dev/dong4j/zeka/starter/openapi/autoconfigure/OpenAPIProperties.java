package dev.dong4j.zeka.starter.openapi.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 组件配置类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:54
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = OpenAPIProperties.PREFIX)
public class OpenAPIProperties extends ZekaProperties {
    /** 组件配置前缀 */
    public static final String PREFIX = ConfigKey.PREFIX + "openapi";
}
