package dev.dong4j.zeka.starter.rest.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.enums.LibraryEnum;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.reactive.WebFluxAutoConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.servlet.ServletAutoConfiguration;
import dev.dong4j.zeka.starter.rest.runner.OpenBrowserRunner;
import dev.dong4j.zeka.starter.rest.spi.RestLauncherInitiation;
import dev.dong4j.zeka.starter.rest.support.ZekaRestComponent;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * 组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.06.29 10:44
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnEnabled(prefix = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
@EnableConfigurationProperties(RestProperties.class)
@ConditionalOnClass(RestLauncherInitiation.class)
@AutoConfigureAfter({
    ServletAutoConfiguration.class,
    WebFluxAutoConfiguration.class
})
public class RestAutoConfiguration implements ZekaAutoConfiguration {

    public RestAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * Gets library type *
     *
     * @return the library type
     * @since 1.0.0
     */
    @Override
    public LibraryEnum getLibraryType() {
        return LibraryEnum.REST;
    }

    /**
     * 定义组件标识 bean
     *
     * @return the component bean
     * @since 1.7.1
     */
    @Primary
    @Bean(App.Components.REST_SPRING_BOOT)
    public ZekaRestComponent restComponent() {
        return new ZekaRestComponent();
    }

    /**
     * 参数验证快速失败, 默认是验证完所有参数然后将所有错误信息一起返回, 这里在生成环境时修改为快速失败模式, 提高验证效率.
     *
     * @return the validator
     * @since 1.0.0
     */
    @Bean
    @Profile(value = {App.ENV_PROD})
    public Validator validator() {
        log.info("参数验证开启快速失败模式");
        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class)
            .configure()
            // 快速失败模式
            .failFast(true)
            // 代替默认的 EL 表达式
            .messageInterpolator(new ParameterMessageInterpolator())
            .buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    /**
     * Open browser runner open browser runner
     *
     * @param restProperties rest properties
     * @return the open browser runner
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenBrowserRunner openBrowserRunner(@NotNull RestProperties restProperties) {
        return new OpenBrowserRunner(restProperties.isEnableBrowser());
    }
}
