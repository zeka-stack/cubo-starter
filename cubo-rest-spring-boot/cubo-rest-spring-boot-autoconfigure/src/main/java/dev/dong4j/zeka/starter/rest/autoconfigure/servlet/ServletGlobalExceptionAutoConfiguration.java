package dev.dong4j.zeka.starter.rest.autoconfigure.servlet;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.web.exception.ServletGlobalExceptionHandler;
import dev.dong4j.zeka.kernel.web.handler.ServletErrorController;
import dev.dong4j.zeka.starter.rest.advice.RestGlobalExceptionHandler;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.ZekaServletExceptionErrorAttributes;
import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:23
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(before = ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties(ServerProperties.class)
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, ZekaServletExceptionErrorAttributes.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletGlobalExceptionAutoConfiguration implements ZekaAutoConfiguration {

    public ServletGlobalExceptionAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * Error attributes default error attributes
     *
     * @return the default error attributes
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new ZekaServletExceptionErrorAttributes(!ConfigKit.isProd());
    }

    /**
     * Basic error controller basic error controller
     *
     * @param errorAttributes  error attributes
     * @param serverProperties server properties
     * @return the basic error controller
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes,
                                                     @NotNull ServerProperties serverProperties) {
        return new ServletErrorController(errorAttributes, serverProperties.getError());
    }

    /**
     * Rest global exception handler
     *
     * @return the servlet global exception handler
     * @since 2022.1.1
     */
    @Bean
    public ServletGlobalExceptionHandler restGlobalExceptionHandler() {
        return new RestGlobalExceptionHandler();
    }
}
