package dev.dong4j.zeka.starter.openapi.spi;

import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.kernel.common.support.ChainMap;
import dev.dong4j.zeka.processor.annotation.AutoService;
import java.util.Map;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>Description: rest 加载默认配置 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class OpenAPILauncherInitiation implements LauncherInitiation {

    @Override
    public Map<String, Object> setDefaultProperties(ConfigurableEnvironment env,
                                                    String appName,
                                                    boolean isLocalLaunch) {

        return ChainMap.build(16)
            // 序列化时只包含不为空的字段
            .put(ConfigKey.PREFIX + "openapi.enabled", "true")
            .put("springdoc.swagger-ui.path", "/swagger-ui.html")
            .put("springdoc.swagger-ui.tags-sorter", "alpha")
            .put("springdoc.swagger-ui.operations-sorter", "alpha")
            .put("springdoc.api-docs.path", "/v3/api-docs")
            .put("springdoc.group-configs[0].group", appName)
            .put("springdoc.group-configs[0].paths-to-match", "/**")
            .put("springdoc.group-configs[0].packages-to-scan", App.BASE_PACKAGES)
            .put("knife4j.enable", "true")
            .put("knife4j.setting.language", "zh_cn");
    }

    /**
     * Gets order *
     *
     * @return the order
     * @since 1.0.0
     */
    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1000;
    }

    @Override
    public String getName() {
        return "cubo-openapi-knife4j-spring-boot-starter";
    }

}
