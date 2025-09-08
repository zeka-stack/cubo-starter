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
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * Reactive 环境下的启动信息自动配置类
 *
 * 该类为 Spring WebFlux Reactive Web 环境提供启动信息和 Git 信息相关的配置。
 * 主要包括：
 *
 * 1. 配置処理启动信息的路由函数，支持重定向到 Git 信息端点
 * 2. 提供读取 Git 属性信息的方法
 * 3. 配置 Reactive 环境下的初始化服务
 *
 * 仅在 Reactive Web 环境下生效，使用条件注解确保兼容性。
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.03.10 11:20
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnEnabled(value = EndpointProperties.PREFIX)
@ConditionalOnClass(value = {WebFluxConfigurer.class, ReactiveEndpointLauncherInitiation.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveStartInfoAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 构造方法
     * <p>
     * 输出启动日志，标识该自动配置类已被加载。
     */
    public ReactiveStartInfoAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 配置启动信息路由函数
     *
     * 如果在 classpath 下存在 git.properties 文件，则将访问 /start 的请求
     * 重定向到 /git 端点，用于显示 Git 相关信息。
     *
     * 使用 Reactive 方式处理请求和响应，支持非阻塞式操作。
     *
     * @return 路由函数定义
     * @since 1.0.0
     */
    @NotNull
    @Bean
    static RouterFunction<ServerResponse> routerFunction() {
        // 创建路由函数，将 GET /start 请求重定向到 Git 信息端点
        return RouterFunctions.route(
            // 匹配 GET 请求到 /start 路径
            RequestPredicates.GET(LibraryEnum.START_URL),
            // 返回临时重定向响应，重定向到本地 Git 信息端点
            request -> ServerResponse.temporaryRedirect(URI.create("http://"
                + NetUtils.getLocalHost()
                + ":"
                + ConfigKit.getPort()
                + ConfigKit.getContextPath()
                + "/git")).build());
    }

    /**
     * 获取 Git 相关信息
     *
     * 读取 classpath 下的 git.properties 文件并返回其内容。
     * 如果文件不存在或读取失败，则返回简单的 "up" 字符串。
     * 该方法使用 @ReadOperation 注解，可以通过 Actuator 端点访问。
     *
     * @return Git 属性信息或默认状态
     * @since 1.0.0
     */
    @ReadOperation
    public Result<? extends Serializable> git() {
        // 获取类加载器
        ClassLoader classLoader = this.getClass().getClassLoader();
        // 尝试加载 git.properties 文件
        InputStream inputStream = classLoader.getResourceAsStream(App.GIT_CONFIG_FILE_NAME);
        // 如果文件存在，读取并返回其内容
        if (inputStream != null) {
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
            } catch (IOException ignored) {
                // 忽略读取异常
            }
            return R.succeed(properties);
        }
        // 文件不存在时返回默认状态
        return R.succeed("up");
    }

    /**
     * 创建 Reactive 初始化服务
     *
     * 在 Reactive 环境下创建初始化服务实现，用于应用预热。
     * 使用 WebClient.Builder 来构建 HTTP 客户端进行异步请求。
     * 仅在没有其他 InitializationService 实现时才会创建。
     *
     * @param webClientBuilder WebClient 构建器，由 Spring Boot 自动配置提供
     * @return ReactiveInitializationService 实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(InitializationService.class)
    public InitializationService reactiveInitializationService(WebClient.Builder webClientBuilder) {
        return new ReactiveInitializationService(webClientBuilder);
    }
}
