package dev.dong4j.zeka.starter.rest.autoconfigure.servlet;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.CollectionUtils;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.autoconfigure.supportss.XssProperties;
import dev.dong4j.zeka.starter.rest.handler.CustomizeReturnValueHandler;
import dev.dong4j.zeka.starter.rest.handler.ZekaServletExceptionErrorAttributes;
import dev.dong4j.zeka.starter.rest.interceptor.AuthenticationInterceptor;
import dev.dong4j.zeka.starter.rest.interceptor.CurrentUserInterceptor;
import dev.dong4j.zeka.starter.rest.interceptor.TraceInterceptor;
import dev.dong4j.zeka.starter.rest.support.CurrentUserArgumentResolver;
import dev.dong4j.zeka.starter.rest.support.CurrentUserService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * <p>Description: rest 自动装配</p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:41
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(value = {ServerProperties.class, XssProperties.class})
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, ZekaServletExceptionErrorAttributes.class})
@ConditionalOnEnabled(prefix = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletAutoConfiguration implements ZekaAutoConfiguration {

    public ServletAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * Current user argument resolver
     *
     * @return the current user argument resolver
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean
    public CurrentUserArgumentResolver currentUserArgumentResolver() {
        return new CurrentUserArgumentResolver();
    }

    /**
     * Current user service
     *
     * @return the current user service
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean
    public CurrentUserService currentUserService() {
        return new CurrentUserService() {
        };
    }

    /**
     * Current user interceptor
     *
     * @param currentUserService current user service
     * @return the current user interceptor
     * @since 1.6.0
     */
    @Bean
    @ConditionalOnMissingBean
    public CurrentUserInterceptor currentUserInterceptor(CurrentUserService currentUserService) {
        return new CurrentUserInterceptor(currentUserService);
    }

    /**
     * Authentication interceptor
     *
     * @param currentUserService current user service
     * @return the authentication interceptor
     * @since 2.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationInterceptor authenticationInterceptor(CurrentUserService currentUserService) {
        return new AuthenticationInterceptor(currentUserService);
    }

    /**
     * 默认使用 uuid 作为 traceId, 如果使用了 tracer 组件则会被替换
     *
     * @return the trace interceptor
     * @since 2022.1.1
     */
    @Bean
    @ConditionalOnMissingBean(TraceInterceptor.class)
    public TraceInterceptor restTraceInterceptor() {
        return new TraceInterceptor();
    }


    /**
     * <p>Description: 使用 {@link ServletWebAutoConfiguration} 配置的 {@link HandlerMethodReturnValueHandler} 的优先级最低, 因此使用这种方式修改</p>
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.12.07 14:40
     * @since 1.7.0
     */
    @Slf4j
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(RequestMappingHandlerAdapter.class)
    @ConditionalOnEnabled(prefix = RestProperties.PREFIX)
    static class ConsumerMethodReturnValueHandlerAutoConfiguration implements ZekaAutoConfiguration {
        /** Adapter */
        private final RequestMappingHandlerAdapter adapter;

        public ConsumerMethodReturnValueHandlerAutoConfiguration(ObjectProvider<RequestMappingHandlerAdapter> adapter) {
            log.info("启动自动配置: [{}]", this.getClass());
            if (adapter.getIfAvailable() != null) {
                this.adapter = adapter.getIfAvailable();
            } else {
                this.adapter = null;
            }
        }

        /**
         * After properties set
         *
         * @since 1.7.0
         */
        @Override
        public void afterPropertiesSet() {
            if (this.adapter != null) {
                List<HandlerMethodReturnValueHandler> returnValueHandlers = this.adapter.getReturnValueHandlers();
                List<HandlerMethodReturnValueHandler> handlers = Collections.emptyList();
                if (CollectionUtils.isNotEmpty(returnValueHandlers)) {
                    handlers = new ArrayList<>(returnValueHandlers);
                }
                this.decorateHandlers(handlers);
                this.adapter.setReturnValueHandlers(handlers);
            }
        }

        /**
         * 重新设置优先级
         *
         * @param handlers handlers
         * @since 1.7.0
         */
        private void decorateHandlers(List<HandlerMethodReturnValueHandler> handlers) {
            for (HandlerMethodReturnValueHandler handler : handlers) {
                if (handler instanceof RequestResponseBodyMethodProcessor) {
                    CustomizeReturnValueHandler decorator = new CustomizeReturnValueHandler(
                        (RequestResponseBodyMethodProcessor) handler);
                    int index = handlers.indexOf(handler);
                    handlers.set(index, decorator);
                    break;
                }
            }
        }
    }
}
