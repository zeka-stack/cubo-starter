package dev.dong4j.zeka.starter.mybatis.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 组件配置类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 16:49
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = MybatisProperties.PREFIX)
public class MybatisProperties extends ZekaProperties {
    /** 组件配置前缀 */
    public static final String PREFIX = "zeka-stack.mybatis";
}
