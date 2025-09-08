package dev.dong4j.zeka.starter.rest.spi;

import dev.dong4j.zeka.kernel.common.start.LauncherInitiation;
import dev.dong4j.zeka.processor.annotation.AutoService;

/**
 * <p>Description: rest 加载默认配置 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 11:17
 * @since 1.0.0
 */
@AutoService(LauncherInitiation.class)
public class ServletLauncherInitiation extends RestLauncherInitiation {
    /**
     * Gets name *
     *
     * @return the name
     * @since 1.0.0
     */
    @Override
    public String getName() {
        return "cubo-rest-servlet-spring-boot-starter";
    }
}
