package dev.dong4j.zeka.starter.endpoint.autoconfigure.reactive;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.api.R;
import dev.dong4j.zeka.kernel.common.api.Result;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.enums.LibraryEnum;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.common.util.NetUtils;
import dev.dong4j.zeka.starter.endpoint.ReactiveEndpointLauncherInitiation;
import dev.dong4j.zeka.starter.endpoint.autoconfigure.EndpointProperties;
import dev.dong4j.zeka.starter.endpoint.initialization.InitializationService;
import dev.dong4j.zeka.starter.endpoint.reactive.ReactiveInitializationService;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * <p>Description:  </p>
 *
 * @author dong4j
 * @version 1.3.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:20
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnEnabled(value = EndpointProperties.PREFIX)
@ConditionalOnClass(value = {WebFluxConfigurer.class, ReactiveEndpointLauncherInitiation.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveStartInfoAutoConfiguration implements ZekaAutoConfiguration {

    public ReactiveStartInfoAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 如果在 classpath 下存在 git.properties, 则重定向到 /git
     *
     * @return the router function
     * @since 1.0.0
     */
    @NotNull
    @Bean
    static RouterFunction<ServerResponse> routerFunction() {
        return RouterFunctions.route(
            RequestPredicates.GET(LibraryEnum.START_URL),
            request -> ServerResponse.temporaryRedirect(URI.create("http://"
                + NetUtils.getLocalHost()
                + ":"
                + ConfigKit.getPort()
                + ConfigKit.getContextPath()
                + "/git")).build());
    }

    /**
     * Git result.
     *
     * @return the result
     * @since 1.0.0
     */
    @ReadOperation
    public Result<? extends Serializable> git() {
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(App.GIT_CONFIG_FILE_NAME);
        if (inputStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
            } catch (IOException ignored) {
            }
            return R.succeed(properties);
        }
        return R.succeed("up");
    }

    /**
     * Reactive initialization service
     *
     * @param webClientBuilder web client builder
     * @return the initialization service
     * @since 2022.1.1
     */
    @Bean
    @ConditionalOnMissingBean(InitializationService.class)
    public InitializationService reactiveInitializationService(WebClient.Builder webClientBuilder) {
        return new ReactiveInitializationService(webClientBuilder);
    }
}
