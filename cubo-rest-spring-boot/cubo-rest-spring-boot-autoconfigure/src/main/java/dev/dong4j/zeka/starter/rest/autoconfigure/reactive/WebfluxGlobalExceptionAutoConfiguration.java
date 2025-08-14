package dev.dong4j.zeka.starter.rest.autoconfigure.reactive;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.JsonErrorWebExceptionHandler;
import dev.dong4j.zeka.starter.rest.handler.ZekaWebfluxExceptionErrorAttributes;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.support.DefaultServerCodecConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.view.ViewResolver;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.20 10:24
 * @since 1.0.0
 */
@Slf4j
@ConditionalOnClass(value = {WebFluxConfigurer.class, ZekaWebfluxExceptionErrorAttributes.class})
@AutoConfiguration(before = WebFluxAutoConfiguration.class)
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(value = {ServerProperties.class, WebProperties.class})
public class WebfluxGlobalExceptionAutoConfiguration implements ZekaAutoConfiguration {
    /** Server properties */
    private final ServerProperties serverProperties;
    /** Application context */
    private final ApplicationContext applicationContext;
    /** Resource properties */
    private final WebProperties resourceProperties;
    /** View resolvers */
    private final List<ViewResolver> viewResolvers;
    /** Server codec configurer */
    private final ServerCodecConfigurer serverCodecConfigurer;

    /**
     * Instantiates a new Custom error web flux auto configuration.
     *
     * @param serverProperties      the server properties
     * @param resourceProperties    the resource properties
     * @param viewResolversProvider the view resolvers provider
     * @param serverCodecConfigurer the server codec configurer
     * @param applicationContext    the application context
     * @since 1.0.0
     */
    public WebfluxGlobalExceptionAutoConfiguration(ServerProperties serverProperties,
                                                   WebProperties resourceProperties,
                                                   @NotNull ObjectProvider<ViewResolver> viewResolversProvider,
                                                   ServerCodecConfigurer serverCodecConfigurer,
                                                   ApplicationContext applicationContext) {
        log.info("启动自动配置: [{}]", this.getClass());

        this.serverProperties = serverProperties;
        this.applicationContext = applicationContext;
        this.resourceProperties = resourceProperties;
        this.viewResolvers = viewResolversProvider.orderedStream().collect(Collectors.toList());
        this.serverCodecConfigurer = serverCodecConfigurer;
    }

    /**
     * Server codec configurer server codec configurer
     *
     * @return the server codec configurer
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return new DefaultServerCodecConfigurer();
    }

    /**
     * 自定义全局异常处理, 替代 DefaultErrorWebExceptionHandler
     *
     * @param errorAttributes the error attributes
     * @return the error web exception handler
     * @since 1.0.0
     */
    @Bean
    @Order(-2)
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes) {
        log.info("初始化自定义全局异常处理器: {}", JsonErrorWebExceptionHandler.class);
        JsonErrorWebExceptionHandler exceptionHandler = new JsonErrorWebExceptionHandler(errorAttributes,
            this.resourceProperties.getResources(),
            this.serverProperties.getError(),
            this.applicationContext);
        exceptionHandler.setViewResolvers(this.viewResolvers);
        exceptionHandler.setMessageWriters(this.serverCodecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(this.serverCodecConfigurer.getReaders());
        return exceptionHandler;
    }

    /**
     * Error attributes default error attributes.
     *
     * @return the default error attributes
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new ZekaWebfluxExceptionErrorAttributes(!ConfigKit.isProd());
    }

}

