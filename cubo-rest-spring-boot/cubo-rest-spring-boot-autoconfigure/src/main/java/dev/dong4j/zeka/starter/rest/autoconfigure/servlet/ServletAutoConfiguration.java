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
import jakarta.servlet.Servlet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * Servlet Web 环境 REST 自动配置类
 * <p>
 * 该自动配置类专门为基于 Servlet 的传统 Web 应用提供 REST 相关的自动配置支持。
 * 它负责注册和配置 Servlet 环境下所需的各种组件，包括拦截器、参数解析器、
 * 返回值处理器等核心组件。
 * <p>
 * 主要功能：
 * 1. 用户信息处理：注册当前用户参数解析器和相关服务
 * 2. 请求拦截：配置用户认证、链路追踪等拦截器
 * 3. 返回值处理：自定义返回值处理器，实现统一响应格式
 * 4. 错误处理：集成自定义的异常属性处理器
 * 5. XSS 防护：集成 XSS 攻击防护机制
 * <p>
 * 注册的核心组件：
 * - {@link CurrentUserArgumentResolver}：当前用户参数解析器
 * - {@link CurrentUserService}：当前用户信息服务
 * - {@link CurrentUserInterceptor}：当前用户拦截器
 * - {@link AuthenticationInterceptor}：Token 认证拦截器
 * - {@link TraceInterceptor}：链路追踪拦截器
 * - {@link CustomizeReturnValueHandler}：自定义返回值处理器
 * <p>
 * 条件化加载：
 * 1. @ConditionalOnWebApplication(SERVLET)：仅在 Servlet Web 环境下生效
 * 2. @ConditionalOnClass：检测 Servlet 和 Spring MVC 相关类的存在
 * 3. @ConditionalOnEnabled：支持通过配置文件开关功能
 * 4. @ConditionalOnMissingBean：避免与用户自定义 Bean 冲突
 * <p>
 * 内部配置类：
 * 包含一个内部的 ConsumerMethodReturnValueHandlerAutoConfiguration 配置类，
 * 用于处理返回值处理器的优先级问题。
 * <p>
 * 设计特点：
 * - 模块化设计：各个功能组件独立注册，便于维护和扩展
 * - 条件化配置：支持用户自定义覆盖默认行为
 * - 优先级处理：通过内部配置类解决组件加载顺序问题
 * - 集成性：与 Spring Boot 和 Spring MVC 的现有配置紧密集成
 * <p>
 * 使用场景：
 * - 传统的 Spring Boot Web 应用
 * - REST API 服务
 * - 微服务中的业务服务
 * - 需要用户认证和权限控制的 Web 应用
 * <p>
 * 注意事项：
 * - 该配置类与 WebFlux 环境下的配置类互斥
 * - 部分组件依赖特定的三方库，如 JWT 处理等
 * - 建议与框架其他 Web 相关组件配合使用
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:41
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(value = {ServerProperties.class, XssProperties.class})
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, ZekaServletExceptionErrorAttributes.class})
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletAutoConfiguration implements ZekaAutoConfiguration {

    /**
     * 注册当前用户参数解析器
     * <p>
     * 该方法注册了一个用于解析 @CurrentUser 注解参数的解析器。
     * 它能够自动从 HTTP 请求中提取用户信息，并将其注入到
     * Controller 方法的参数中，简化用户信息的获取逻辑。
     * <p>
     * 功能特点：
     * - 支持从 JWT Token 中自动解析用户信息
     * - 支持从 HTTP 请求头中获取用户标识
     * - 提供默认的异常处理机制
     *
     * @return 当前用户参数解析器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public CurrentUserArgumentResolver currentUserArgumentResolver() {
        return new CurrentUserArgumentResolver();
    }

    /**
     * 注册当前用户信息服务
     * <p>
     * 该方法注册了一个默认的当前用户信息服务实现。这个服务提供了
     * 从 HTTP 请求中提取用户信息的标准方法，包括 JWT Token 解析、
     * 请求头处理等功能。
     * <p>
     * 默认实现特点：
     * - 使用接口的默认方法实现
     * - 支持基于 JWT 的用户认证
     * - 提供扩展点供用户自定义实现
     * - 与框架的认证机制集成
     *
     * @return 当前用户信息服务的默认实现
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public CurrentUserService currentUserService() {
        return new CurrentUserService() {
        };
    }

    /**
     * 注册当前用户拦截器
     * <p>
     * 该方法注册了一个用于处理当前用户信息的拦截器。该拦截器会
     * 在请求处理过程中自动解析和设置当前用户信息，为后续的
     * Controller 方法提供用户上下文。
     * <p>
     * 功能说明：
     * - 在请求进入 Controller 之前解析用户信息
     * - 为后续的参数解析器提供用户上下文
     * - 支持用户信息的缓存和传递
     * - 与 Spring Security 等安全框架集成
     *
     * @param currentUserService 当前用户信息服务，用于解析用户信息
     * @return 当前用户拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public CurrentUserInterceptor currentUserInterceptor(CurrentUserService currentUserService) {
        return new CurrentUserInterceptor(currentUserService);
    }

    /**
     * 注册认证拦截器
     * <p>
     * 该方法注册了一个用于处理 API 认证的拦截器。该拦截器基于
     *
     * @param currentUserService 当前用户信息服务，用于验证和解析用户信息
     * @return 认证拦截器实例
     * @TokenRequired 注解进行工作，只有标记了该注解的 API 才会进行
     * Token 认证检查。
     * <p>
     * 认证特点：
     * - 基于注解的认证机制，精确控制到方法级别
     * - 支持 JWT Token 的验证和解析
     * - 提供明确的错误信息和异常处理
     * - 不影响 API 文档的可读性
     * <p>
     * 优势：
     * - 相比于在 Controller 方法中直接注入 CurrentUser
     * - 避免在 Swagger 等 API 文档中显示不必要的参数
     * - 提供更加灵活和精准的认证控制
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthenticationInterceptor authenticationInterceptor(CurrentUserService currentUserService) {
        return new AuthenticationInterceptor(currentUserService);
    }

    /**
     * 注册链路追踪拦截器
     * <p>
     * 该方法注册了一个用于生成和管理链路追踪 ID 的拦截器。
     * 默认情况下使用 UUID 作为 traceId，如果项目中集成了 Tracer
     * 组件（如 Zipkin、Jaeger等），则会被替换为更专业的实现。
     * <p>
     * 功能说明：
     * - 为每个 HTTP 请求生成唯一的链路追踪 ID
     * - 在请求处理过程中保持 traceId 的传递
     * - 支持与分布式追踪系统集成
     * - 在请求结束后自动清理上下文
     * <p>
     * 使用场景：
     * - 分布式系统中的请求链路追踪
     * - 问题排查和日志关联
     * - 性能监控和分析
     * - 业务流程的全链路跟踪
     *
     * @return 链路追踪拦截器实例
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(TraceInterceptor.class)
    public TraceInterceptor restTraceInterceptor() {
        return new TraceInterceptor();
    }


    /**
     * 返回值处理器自动配置类
     * <p>
     * 该内部静态配置类专门用于解决 Spring MVC 中返回值处理器的优先级问题。
     * 由于在 {@link ServletWebAutoConfiguration} 中配置的 {@link HandlerMethodReturnValueHandler}
     * 优先级最低，因此需要通过这种方式来修改和优化处理器的执行顺序。
     * <p>
     * 问题背景：
     * Spring MVC 的返回值处理器是按照注册顺序执行的，而自动配置的
     * 处理器通常最后注册，导致自定义的处理逻辑无法生效。
     * <p>
     * 解决方案：
     * 1. 获取已注册的所有返回值处理器
     * 2. 找到 RequestResponseBodyMethodProcessor 实例
     * 3. 用自定义的 CustomizeReturnValueHandler 装饰器替换它
     * 4. 重新设置处理器列表，确保自定义逻辑生效
     * <p>
     * 数据流转：
     * 原始请求 → RequestMappingHandlerAdapter → CustomizeReturnValueHandler → 统一响师格式
     * <p>
     * 设计特点：
     * - 使用静态内部类，避免不必要的实例引用
     * - 实现 ZekaAutoConfiguration 接口，支持初始化后处理
     * - 条件化加载，只在需要时才激活
     * - 装饰器模式，保留原有功能并增强特性
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2020.12.07 14:40
     * @since 1.0.0
     */
    @Slf4j
    @AutoConfiguration
    @ConditionalOnBean(RequestMappingHandlerAdapter.class)
    @ConditionalOnEnabled(value = RestProperties.PREFIX)
    static class ConsumerMethodReturnValueHandlerAutoConfiguration implements ZekaAutoConfiguration {
        /** Spring MVC 请求映射处理器适配器，用于管理和配置返回值处理器 */
        private final RequestMappingHandlerAdapter adapter;

        /**
         * 构造函数，初始化返回值处理器自动配置
         * <p>
         * 该构造函数使用 ObjectProvider 来延迟获取 RequestMappingHandlerAdapter，
         * 这样可以确保在 Spring 容器完全初始化后再进行处理器的修改。
         *
         * @param adapter RequestMappingHandlerAdapter 提供者，用于延迟获取适配器实例
         */
        public ConsumerMethodReturnValueHandlerAutoConfiguration(ObjectProvider<RequestMappingHandlerAdapter> adapter) {
            log.info("启动自动配置: [{}]", this.getClass());
            if (adapter.getIfAvailable() != null) {
                this.adapter = adapter.getIfAvailable();
            } else {
                this.adapter = null;
            }
        }

        /**
         * 在属性设置完成后执行的初始化方法
         * <p>
         * 该方法会在 Spring 容器初始化完成后被自动调用，用于修改
         * RequestMappingHandlerAdapter 中的返回值处理器列表。
         * <p>
         * 执行流程：
         * 1. 检查 RequestMappingHandlerAdapter 是否可用
         * 2. 获取已注册的所有返回值处理器
         * 3. 创建新的处理器列表副本
         * 4. 对处理器列表进行装饰和优化
         * 5. 重新设置优化后的处理器列表
         *
         * @since 1.0.0
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
         * 装饰和优化返回值处理器列表
         * <p>
         * 该方法会遍历所有的返回值处理器，找到 RequestResponseBodyMethodProcessor
         * 实例并用自定义的 CustomizeReturnValueHandler 装饰器替换它。
         * <p>
         * 替换策略：
         * 1. 遍历所有已注册的返回值处理器
         * 2. 检查是否为 RequestResponseBodyMethodProcessor 类型
         * 3. 如果是，则创建 CustomizeReturnValueHandler 装饰器
         * 4. 在原位置替换成装饰器实例
         * 5. 一旦找到并替换完成，就退出循环
         * <p>
         * 这种设计保证了：
         * - 保持原有处理器的所有功能
         * - 在不影响性能的前提下增加自定义逻辑
         * - 保持与 Spring MVC 框架的兼容性
         *
         * @param handlers 需要装饰的返回值处理器列表
         * @since 1.0.0
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
