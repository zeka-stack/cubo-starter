package dev.dong4j.zeka.starter.{{name}}.autoconfigure;

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
 * @date {{date}}
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = {{Name}}Properties.PREFIX)
public class {{Name}}Properties extends ZekaProperties {
    /** 组件配置前缀 */
    public static final String PREFIX = ConfigKey.PREFIX + "{{name}}";
}
