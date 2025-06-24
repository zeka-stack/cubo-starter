package dev.dong4j.zeka.starter.rest.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 组件配置类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.25 00:11
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = RestProperties.PREFIX)
public class RestProperties {
    /** PREFIX */
    public static final String PREFIX = "zeka-stack.rest";
}
