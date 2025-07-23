package dev.dong4j.zeka.starter.rest.autoconfigure.supportss;

import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p>Description: Xss配置类</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.26 20:43
 * @since 1.0.0
 */
@Data
@ConfigurationProperties(XssProperties.PREFIX)
public class XssProperties {
    /** PREFIX */
    public static final String PREFIX = ConfigKey.PREFIX + "rest.xss";

    /** xss 处理器 */
    private boolean enableXssFilter = Boolean.TRUE;
    /** 设置忽略的 url */
    private List<String> excludePatterns = new ArrayList<>();

}
