package dev.dong4j.zeka.starter.openapi.autoconfigure.knife4j;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.github.xiaoymin.knife4j.spring.filter.ProductionSecurityFilter;
import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.enums.LibraryEnum;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.openapi.autoconfigure.OpenAPIProperties;
import javax.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * <p>Description: Knife4j 基础自动配置类 </p>
 *
 * @author dong4j
 * @version 1.4.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.05.08 16:14
 * @since 1.4.0
 */
@Configuration(proxyBeanMethods = false)
@Profile(value = {App.ENV_NOT_PROD})
@ConditionalOnClass(value = {
    EnableKnife4j.class,
    Servlet.class,
    DispatcherServlet.class,
})
@ConditionalOnEnabled(prefix = OpenAPIProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(value = {Knife4jProperties.class})
@Slf4j
public class Knife4jAutoConfiguration implements ZekaAutoConfiguration {

    public Knife4jAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * Production security filter
     *
     * @return the production security filter
     * @since 1.4.0
     */
    @Bean
    public ProductionSecurityFilter productionSecurityFilter() {
        return new ProductionSecurityFilter(!ConfigKit.isLocalLaunch() && ConfigKit.isProd());
    }

    /**
     * <p>Description: </p>
     *
     * @author dong4j
     * @version 1.4.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.05.08 16:52
     * @since 1.4.0
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnResource(resources = "classpath:META-INF/resources/doc.html")
    static class Knife4jUiAutoConfiguration implements ZekaAutoConfiguration {

        /**
         * Gets library type *
         *
         * @return the library type
         * @since 1.4.0
         */
        @Override
        public LibraryEnum getLibraryType() {
            return LibraryEnum.SWAGGER_REST_BOOTSTRAP;
        }
    }
}
