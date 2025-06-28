package dev.dong4j.zeka.starter.endpoint;

import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.processor.annotation.AutoService;
import dev.dong4j.zeka.starter.endpoint.constant.Endpoint;
import dev.dong4j.zeka.starter.endpoint.spi.EndpointLauncherInitiation;

/**
 * <p>Description: 通过 SPI 加载 endpoint 默认配置</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:12
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class ServletEndpointLauncherInitiation extends EndpointLauncherInitiation {

    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return Endpoint.SERVLET_MODULE_NAME;
    }
}
