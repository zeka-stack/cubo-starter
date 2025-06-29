package dev.dong4j.zeka.starter.rest.support;


import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.start.ZekaComponentBean;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2021.01.16 02:39
 * @since 1.7.1
 */
public class ZekaRestComponent implements ZekaComponentBean {
    /**
     * Component bean
     *
     * @return the string
     * @since 1.7.1
     */
    @Override
    public String componentName() {
        return App.Components.REST_SPRING_BOOT;
    }
}
