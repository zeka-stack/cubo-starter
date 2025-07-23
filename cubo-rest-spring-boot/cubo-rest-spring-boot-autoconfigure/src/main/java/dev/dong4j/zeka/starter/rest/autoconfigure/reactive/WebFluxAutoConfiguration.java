package dev.dong4j.zeka.starter.rest.autoconfigure.reactive;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.ZekaWebfluxExceptionErrorAttributes;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.reactive.context.ReactiveWebApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:47
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@RestController
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@ConditionalOnClass(value = {WebFluxConfigurer.class, ZekaWebfluxExceptionErrorAttributes.class})
@ConditionalOnEnabled(value = RestProperties.PREFIX)
public class WebFluxAutoConfiguration implements ZekaAutoConfiguration {

    public WebFluxAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /** Application context */
    @Resource
    private ReactiveWebApplicationContext applicationContext;

}
