package dev.dong4j.zeka.starter.openapi.autoconfigure.knife4j;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 04:14
 * @since 1.4.0
 */
@Data
@ConfigurationProperties(prefix = Knife4jProperties.PREFIX)
public class Knife4jProperties {
    /** PREFIX */
    public static final String PREFIX = "zeka-stack.openapi.knife4j";

}
