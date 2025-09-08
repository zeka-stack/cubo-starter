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
 * WebFlux 全局异常处理自动配置类
 *
 * 该配置类专门为 WebFlux 反应式环境提供全局异常处理能力。它负责注册和配置
 * 自定义的异常处理器，替代 Spring Boot 默认的异常处理机制，为 WebFlux 应用
 * 提供更加一致和专业的错误响应处理。
 *
 * 主要功能：
 * 1. 注册自定义的 JsonErrorWebExceptionHandler，替代默认异常处理器
 * 2. 配置 ZekaWebfluxExceptionErrorAttributes，提供自定义的错误属性提取
 * 3. 集成 WebFlux 的编解码器配置，支持多种数据格式
 * 4. 配置视图解析器，支持多种视图模板技术
 * 5. 根据环境自动调整异常信息的详细程度
 *
 * 配置组件：
 * - {@link JsonErrorWebExceptionHandler}：自定义的 JSON 格式异常处理器
 * - {@link ZekaWebfluxExceptionErrorAttributes}：自定义的错误属性提取器
 * - {@link ServerCodecConfigurer}：服务器编解码器配置
 *
 * 条件化加载：
 * 1. @ConditionalOnWebApplication(REACTIVE)：仅在反应式 Web 环境下生效
 * 2. @ConditionalOnClass：检测 WebFlux 相关类的存在
 * 3. @ConditionalOnEnabled：支持通过配置文件开关功能
 * 4. @ConditionalOnMissingBean：避免与用户自定义的 Bean 冲突
 *
 * 加载顺序：
 * 通过 @AutoConfiguration(before = WebFluxAutoConfiguration.class) 确保
 * 在 Spring Boot 的 WebFlux 自动配置之前加载，保证配置的优先级。
 *
 * 异常处理优先级：
 * 通过 @Order(-2) 设置高优先级，确保自定义的异常处理器
 * 能够拦截所有未处理的异常。
 *
 * 环境适配：
 * - 开发环境：提供详细的异常信息，包括堆栈跟踪
 * - 生产环境：只提供必要的错误信息，保护系统安全
 *
 * 配置属性：
 * - ServerProperties：服务器配置，包括错误页面路径等
 * - WebProperties：静态资源配置，用于错误页面等
 *
 * 设计特点：
 * - 可覆盖性：允许用户通过自定义 Bean 覆盖默认配置
 * - 模块化：各个组件独立配置，便于维护和扩展
 * - 集成性：与 Spring Boot 和 WebFlux 的现有配置紧密集成
 * - 灵活性：支持条件化加载和配置覆盖
 *
 * @author dong4j
 * @version 1.0.0
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
    /** 服务器配置属性，包含错误处理、端口等配置信息 */
    private final ServerProperties serverProperties;
    /** Spring 应用上下文，用于访问其他 Bean 和组件 */
    private final ApplicationContext applicationContext;
    /** Web 资源配置属性，包含静态资源路径等配置 */
    private final WebProperties resourceProperties;
    /** 视图解析器列表，用于处理错误页面的渲染 */
    private final List<ViewResolver> viewResolvers;
    /** 服务器编解码器配置，用于处理 HTTP 请求和响应的编解码 */
    private final ServerCodecConfigurer serverCodecConfigurer;

    /**
     * 构造函数，初始化 WebFlux 全局异常处理自动配置
     *
     * 该构造函数负责初始化全局异常处理所需的所有组件和配置。
     * 它会收集和设置各种依赖组件，为后续的 Bean 注册做准备。
     *
     * 初始化包括：
     * 1. 服务器基础配置信息
     * 2. Web 资源和静态文件配置
     * 3. 视图解析器集合，支持多种模板技术
     * 4. 编解码器配置，处理请求和响应的数据转换
     * 5. Spring 应用上下文引用
     *
     * @param serverProperties      服务器配置属性，包含端口、错误处理等配置
     * @param resourceProperties    Web 资源配置属性，包含静态资源路径等
     * @param viewResolversProvider 视图解析器提供者，自动收集所有注册的视图解析器
     * @param serverCodecConfigurer 服务器编解码器配置，用于 HTTP 消息的序列化
     * @param applicationContext    Spring 应用上下文，用于访问其他 Bean
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
     * 注册默认的服务器编解码器配置
     *
     * 该方法会在系统中没有其他 ServerCodecConfigurer Bean 时提供一个默认实现。
     * 编解码器配置用于处理 WebFlux 中 HTTP 请求和响应的数据转换，
     * 支持 JSON、XML、表单数据等多种数据格式。
     *
     * 功能特点：
     * - 支持多种数据格式的编解码
     * - 与 WebFlux 的响应式流处理机制集成
     * - 可以被用户自定义的配置覆盖
     *
     * @return 默认的服务器编解码器配置实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerCodecConfigurer serverCodecConfigurer() {
        return new DefaultServerCodecConfigurer();
    }

    /**
     * 注册自定义的全局异常处理器
     *
     * 该方法创建并配置了自定义的 JsonErrorWebExceptionHandler，用于替代
     * Spring Boot 默认的 DefaultErrorWebExceptionHandler。这个自定义处理器
     * 会将所有异常转换为统一的 JSON 格式响师。
     *
     * 配置包括：
     * 1. 错误属性提取器：用于从异常中提取信息
     * 2. 资源配置：用于错误页面等静态资源的处理
     * 3. 服务器错误配置：包括错误页面路径等
     * 4. 应用上下文：用于访问其他 Spring Bean
     * 5. 视图解析器：用于处理模板渲染
     * 6. 消息编解码器：用于 HTTP 消息的序列化和反序列化
     *
     * 优先级设置：
     * 通过 @Order(-2) 设置高优先级，确保在所有其他异常处理器
     * 之前被调用，实现真正的全局异常拦截。
     *
     * @param errorAttributes 错误属性提取器，由 Spring 自动注入
     * @return 配置完成的自定义异常处理器
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
     * 注册自定义的错误属性提取器
     *
     * 该方法创建了自定义的 ZekaWebfluxExceptionErrorAttributes，替代 Spring Boot
     * 默认的 DefaultErrorAttributes。这个自定义实现会根据环境配置提供
     * 不同级别的错误信息。
     *
     * 环境适配特性：
     * - 开发环境 (!ConfigKit.isProd())：
     *   * 包含详细的异常信息，如堆栈跟踪
     *   * 包含请求参数和请求头信息
     *   * 方便开发人员进行问题排查
     *
     * - 生产环境 (ConfigKit.isProd())：
     *   * 只返回基本的错误信息
     *   * 保护系统内部信息安全
     *   * 避免泄露敏感的技术细节
     *
     * 条件化注册：
     * 只有在当前上下文中没有其他 ErrorAttributes 类型的 Bean 时
     * 才会注册该实例，保证用户可以通过自定义 Bean 覆盖默认行为。
     *
     * @return 按环境配置的错误属性提取器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public DefaultErrorAttributes errorAttributes() {
        return new ZekaWebfluxExceptionErrorAttributes(!ConfigKit.isProd());
    }

}

