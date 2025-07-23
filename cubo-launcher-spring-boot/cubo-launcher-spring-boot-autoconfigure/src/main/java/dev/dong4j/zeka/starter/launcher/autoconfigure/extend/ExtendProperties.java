package dev.dong4j.zeka.starter.launcher.autoconfigure.extend;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.01.28 01:27
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(prefix = ExtendProperties.PREFIX)
public class ExtendProperties {

    /** PREFIX */
    public static final String PREFIX = ConfigKey.PREFIX + "extend";
    /** 是否允许 @Resource 注入的 bean 为 null */
    private boolean enableResourceIsNull = Boolean.FALSE;
    /** 全局设置是否允许 @Autowired 注入的 bean 为 null */
    private boolean enableAutowiredIsNull = Boolean.FALSE;
}
