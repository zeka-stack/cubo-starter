package dev.dong4j.zeka.starter.rest.autoconfigure.servlet;

import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.ConfigKit;
import dev.dong4j.zeka.kernel.web.exception.ServletGlobalExceptionHandler;
import dev.dong4j.zeka.kernel.web.handler.ServletErrorController;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.handler.ZekaServletExceptionErrorAttributes;
import jakarta.servlet.Servlet;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
 * Servlet 全局异常处理自动配置类
 * <p>
 * 该配置类专门为传统的 Servlet Web 环境提供全局异常处理能力。它负责注册和配置
 * 自定义的错误属性提取器和错误控制器，替代 Spring Boot 默认的异常处理机制，
 * 为 Spring MVC 应用提供更加一致和专业的错误响应处理。
 * <p>
 * 主要功能：
 * 1. 注册自定义的 ZekaServletExceptionErrorAttributes，替代默认错误属性提取器
 * 2. 配置自定义的 ServletErrorController，处理错误路由和页面渲染
 * 3. 根据环境自动调整异常信息的详细程度
 * 4. 集成框架的全局异常处理器，提供统一的异常处理体验
 * 5. 支持自定义错误页面和错误响应格式
 * <p>
 * 配置组件：
 * - {@link ZekaServletExceptionErrorAttributes}：自定义的错误属性提取器
 * - {@link ServletErrorController}：自定义的错误控制器，继承自 BasicErrorController
 * - 集成 ServletGlobalExceptionHandler，提供业务异常处理能力
 * <p>
 * 条件化加载：
 * 1. @ConditionalOnWebApplication(SERVLET)：仅在 Servlet Web 环境下生效
 * 2. @ConditionalOnClass：检测 Servlet 和 Spring MVC 相关类的存在
 * 3. @ConditionalOnEnabled：支持通过配置文件开关功能
 * 4. @ConditionalOnMissingBean：避免与用户自定义 Bean 冲突
 * <p>
 * 加载顺序：
 * 通过 @AutoConfiguration(before = ErrorMvcAutoConfiguration.class) 确保
 * 在 Spring Boot 的 ErrorMvcAutoConfiguration 之前加载，保证配置的优先级。
 * <p>
 * 环境适配：
 * - 开发环境：提供详细的异常信息，包括堆栈跟踪
 * - 生产环境：只提供必要的错误信息，保护系统安全
 * <p>
 * 配置属性：
 * - ServerProperties：服务器配置，包括错误页面路径等
 * - RestProperties：框架 REST 模块的配置属性
 * <p>
 * 与其他组件的关系：
 * - 替代 Spring Boot 默认的 BasicErrorController
 * - 替代 Spring Boot 默认的 DefaultErrorAttributes
 * - 集成 ServletGlobalExceptionHandler，实现业务异常的统一处理
 * <p>
 * 设计特点：
 * - 可覆盖性：允许用户通过自定义 Bean 覆盖默认配置
 * - 模块化：各个组件独立配置，便于维护和扩展
 * - 集成性：与 Spring Boot 和 Spring MVC 的现有配置紧密集成
 * - 灵活性：支持条件化加载和配置覆盖
 * <p>
 * 使用场景：
 * - 传统的 Spring Boot MVC 应用
 * - 需要统一异常处理的 Web 服务
 * - 需要自定义错误页面的应用
 * - 微服务中的业务服务
 * <p>
 * 注意事项：
 * - 该配置类与 WebFlux 环境下的配置类互斥
 * - 需要确保项目中包含 Servlet 相关依赖
 * - 建议与框架其他 Web 相关组件配合使用
 *
 * @author dong4j
 * @version 1.0.0
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

    /**
     * 构造函数，初始化 Servlet 全局异常处理自动配置
     * <p>
     * 在配置类实例化时被调用，用于记录配置的加载信息。
     * 这有助于在应用启动时跟踪哪些自动配置被激活，
     * 便于问题排查和系统监控。
     */
    public ServletGlobalExceptionAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 注册自定义的错误属性提取器
     * <p>
     * 该方法创建了自定义的 ZekaServletExceptionErrorAttributes，替代 Spring Boot
     * 默认的 DefaultErrorAttributes。这个自定义实现会根据环境配置提供
     * 不同级别的错误信息。
     * <p>
     * 环境适配特性：
     * - 开发环境 (!ConfigKit.isProd())：
     * * 包含详细的异常信息，如堆栈跟踪
     * * 包含请求参数和请求头信息
     * * 方便开发人员进行问题排查
     * <p>
     * - 生产环境 (ConfigKit.isProd())：
     * * 只返回基本的错误信息
     * * 保护系统内部信息安全
     * * 避免泄露敏感的技术细节
     * <p>
     * 条件化注册：
     * 只有在当前上下文中没有其他 ErrorAttributes 类型的 Bean 时
     * 才会注册该实例，保证用户可以通过自定义 Bean 覆盖默认行为。
     *
     * @return 按环境配置的错误属性提取器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class)
    public DefaultErrorAttributes errorAttributes() {
        return new ZekaServletExceptionErrorAttributes(!ConfigKit.isProd());
    }

    /**
     * 注册自定义的错误控制器
     * <p>
     * 该方法创建了自定义的 ServletErrorController，替代 Spring Boot 默认的
     * BasicErrorController。这个自定义控制器集成了框架的全局异常处理器，
     * 提供更加专业和统一的错误处理能力。
     * <p>
     * 功能特性：
     * 1. 继承 BasicErrorController 的所有功能
     * 2. 集成 ServletGlobalExceptionHandler，实现业务异常的统一处理
     * 3. 支持自定义错误页面和响应格式
     * 4. 与框架的其他组件无缝集成
     * <p>
     * 依赖组件：
     * - ErrorAttributes：用于提取错误信息的组件
     * - ServerProperties：服务器配置，包含错误处理相关配置
     * - ServletGlobalExceptionHandler：可选的全局异常处理器
     * <p>
     * 条件化注册：
     * 只有在当前上下文中没有其他 ErrorController 类型的 Bean 时
     * 才会注册该实例，保证用户可以通过自定义 Bean 覆盖默认行为。
     *
     * @param errorAttributes  错误属性提取器，由 Spring 自动注入
     * @param serverProperties 服务器配置属性，包含错误处理配置
     * @param handlerProvider  全局异常处理器提供者，可选依赖
     * @return 配置完成的自定义错误控制器
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class)
    public BasicErrorController basicErrorController(ErrorAttributes errorAttributes,
                                                     @NotNull ServerProperties serverProperties,
                                                     ObjectProvider<ServletGlobalExceptionHandler> handlerProvider) {
        ServletGlobalExceptionHandler servletHandler = handlerProvider.getIfAvailable();
        return new ServletErrorController(errorAttributes, serverProperties.getError(), servletHandler);
    }

}
