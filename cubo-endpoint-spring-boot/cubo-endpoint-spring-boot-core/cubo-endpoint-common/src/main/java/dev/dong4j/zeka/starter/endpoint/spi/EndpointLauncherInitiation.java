package dev.dong4j.zeka.starter.endpoint.spi;

import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.enums.ZekaEnv;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description: 通过 SPI 加载 endpoint 默认配置</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:12
 * @since 1.0.0
 */
public abstract class EndpointLauncherInitiation implements LauncherInitiation {

    /**
     * Launcher chain map
     *
     * @param env           env
     * @param appName       app name
     * @param isLocalLaunch is local launch
     * @return the chain map
     * @since 1.0.0
     */
    @Override
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {
        ChainMap map = ChainMap.build(4)
            .put(ConfigKey.ManagementConfigKey.ENABLED, ConfigDefaultValue.TRUE)
            // 输出更多的 health 信息
            .put(ConfigKey.ManagementConfigKey.HEALTH_DETAILS, "always")
            .put(ConfigKey.ManagementConfigKey.BASE_URL, "/actuator")
            // 输出更多的 git 信息
            .put(ConfigKey.ManagementConfigKey.GIT_MODE, "full");

        if (!ZekaEnv.PROD.getName().equals(ConfigKit.getProfile(env))) {
            map.put(ConfigKey.ManagementConfigKey.EXPOSURE_INCLUDE, "*");
        }

        return map;
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 200;
    }

}
