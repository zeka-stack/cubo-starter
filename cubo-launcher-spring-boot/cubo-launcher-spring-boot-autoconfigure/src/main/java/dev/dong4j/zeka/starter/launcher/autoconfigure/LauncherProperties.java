package dev.dong4j.zeka.starter.launcher.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:54
 * @since 1.0.0
 */
@Getter
@Setter
@ConfigurationProperties(prefix = LauncherProperties.PREFIX)
public class LauncherProperties extends ZekaProperties {

    /** PREFIX */
    public static final String PREFIX = ConfigKey.PREFIX + "app";
    /** 装载自定义配置 zeka-stack.app.custom.xxx */
    private Map<String, Object> custom;
    /** App */
    @Deprecated
    private Map<String, Object> app;
    /** 是否关闭 banner */
    private boolean enableBanner = Boolean.FALSE;
    /** 应用分组 */
    private String group;
    /** Config group */
    private String configGroup;
    /** Discovery group */
    private String discoveryGroup;

    /**
     * Sets app *
     *
     * @param app app
     * @since 1.0.0
     */
    @DeprecatedConfigurationProperty(replacement = ConfigKey.PREFIX + "app.custom", reason = "添加 app 配置层级, 与其他项目区分")
    public void setApp(Map<String, Object> app) {
        this.app = app;
    }

    /**
     * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.3.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.04.19 16:14
     * @since 1.0.0
     */
    public enum Type {
        /** Config type */
        config,
        /** Discovery type */
        discovery
    }
}
