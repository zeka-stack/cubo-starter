package dev.dong4j.zeka.starter.rest.autoconfigure.servlet;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.constant.App;
import dev.dong4j.zeka.kernel.common.constant.ConfigDefaultValue;
import dev.dong4j.zeka.kernel.common.constant.ConfigKey;
import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import dev.dong4j.zeka.kernel.common.enums.serialize.EntityEnumDeserializer;
import dev.dong4j.zeka.kernel.common.enums.serialize.EntityEnumSerializer;
import dev.dong4j.zeka.kernel.common.jackson.MappingApiJackson2HttpMessageConverter;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.Charsets;
import dev.dong4j.zeka.kernel.common.util.SecurityUtils;
import dev.dong4j.zeka.kernel.common.util.StringPool;
import dev.dong4j.zeka.kernel.web.autoconfigure.servlet.WebProperties;
import dev.dong4j.zeka.kernel.web.filter.ExceptionFilter;
import dev.dong4j.zeka.kernel.web.filter.ServletGlobalCacheFilter;
import dev.dong4j.zeka.kernel.web.handler.ServletErrorController;
import dev.dong4j.zeka.kernel.web.util.InnerWebUtils;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import dev.dong4j.zeka.starter.rest.autoconfigure.supportss.JacksonConfiguration;
import dev.dong4j.zeka.starter.rest.autoconfigure.supportss.XssProperties;
import dev.dong4j.zeka.starter.rest.converter.GlobalEnumConverterFactory;
import dev.dong4j.zeka.starter.rest.converter.StringToDateConverter;
import dev.dong4j.zeka.starter.rest.filter.GlobalParameterFilter;
import dev.dong4j.zeka.starter.rest.handler.ZekaServletExceptionErrorAttributes;
import dev.dong4j.zeka.starter.rest.interceptor.AuthenticationInterceptor;
import dev.dong4j.zeka.starter.rest.interceptor.CurrentUserInterceptor;
import dev.dong4j.zeka.starter.rest.mapping.ApiVersionRequestMappingHandlerMapping;
import dev.dong4j.zeka.starter.rest.support.CurrentUserArgumentResolver;
import dev.dong4j.zeka.starter.rest.support.FormdataBodyArgumentResolver;
import dev.dong4j.zeka.starter.rest.support.RequestAbstractFormMethodArgumentResolver;
import dev.dong4j.zeka.starter.rest.support.RequestSingleParamHandlerMethodArgumentResolver;
import dev.dong4j.zeka.starter.rest.xss.XssFilter;
import jakarta.annotation.Resource;
import jakarta.servlet.Servlet;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.ResourceRegionHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.UrlPathHelper;

/**
 * Servlet Web 环境自动配置类
 *
 * 该自动配置类是 Servlet Web 环境下的核心配置组件，负责配置和注册
 * 所有与 Web 请求处理相关的组件。它提供了完整的 Web 层基础设施配置，
 * 包括消息转换器、过滤器、拦截器、参数解析器等。
 *
 * 主要功能：
 * 1. HTTP 消息转换器配置：使用 Jackson 作为 JSON 转换器，支持枚举序列化
 * 2. 跨域请求支持：非生产环境自动开启 CORS 支持
 * 3. 字符编码过滤器：统一设置 UTF-8 编码
 * 4. 全局缓存过滤器：缓存 Request 和 Response 以支持重复读取
 * 5. 异常处理过滤器：在 Filter 层提供异常捕获和处理
 * 6. XSS 防护过滤器：防止跨站脚本攻击
 * 7. 参数注入过滤器：支持全局参数注入机制
 * 8. 自定义参数解析器：支持多种自定义注解的参数解析
 * 9. 拦截器注册：用户认证、链路追踪等拦截器
 * 10. API 版本控制：支持基于 URL 的 API 版本管理
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2019.11.21 21:47
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(after = JacksonConfiguration.class)
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@EnableConfigurationProperties(value = {ServerProperties.class, XssProperties.class, WebProperties.class})
@ConditionalOnClass(value = {Servlet.class, DispatcherServlet.class, ZekaServletExceptionErrorAttributes.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class ServletWebAutoConfiguration implements WebMvcConfigurer, ZekaAutoConfiguration {

    /** Object mapper */
    @Resource
    private ObjectMapper objectMapper;
    /** Current user argument resolver */
    @Resource
    private CurrentUserArgumentResolver currentUserArgumentResolver;
    /** Current user interceptor */
    @Resource
    private CurrentUserInterceptor currentUserInterceptor;
    /** Authentication interceptor */
    @Resource
    private AuthenticationInterceptor authenticationInterceptor;
    /** GLOBAL_ENUM_CONVERTER_FACTORY */
    private static final ConverterFactory<String, SerializeEnum<?>> GLOBAL_ENUM_CONVERTER_FACTORY = new GlobalEnumConverterFactory();
    /** MAX_AGE */
    private static final Long MAX_AGE = 18000L;

    /**
     * 构造函数，初始化 Servlet Web 自动配置
     *
     * @since 1.0.0
     */
    public ServletWebAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 注册字符编码过滤器
     *
     * 设置字符过滤器，强制设置请求和响应的字符编码为 UTF-8。
     * 这确保了整个应用在处理中文等多字节字符时的一致性和正确性。
     *
     * @return 配置好的字符编码过滤器注册Bean
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(CharacterEncodingFilter.class)
    public FilterRegistrationBean<CharacterEncodingFilter> characterEncodingFilterFilterRegistrationBean() {
        FilterRegistrationBean<CharacterEncodingFilter> registrationBean = new FilterRegistrationBean<>();
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding(StringPool.UTF_8);
        characterEncodingFilter.setForceEncoding(true);
        registrationBean.setFilter(characterEncodingFilter);
        return registrationBean;
    }

    /**
     * 注册全局缓存过滤器
     *
     * request 和 response 缓存过滤器，用于缓存 HTTP 请求和响应的内容，
     * 允许在请求处理过程中多次读取请求体和响应体。
     *
     * @param properties Web 配置属性，包含缓存忽略的 URL 模式等配置
     * @return 配置好的全局缓存过滤器注册Bean
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ServletGlobalCacheFilter.class)
    @ConditionalOnProperty(
        value = ConfigKey.WEB_ENABLE_GLOBAL_CACHE_FILTER,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public FilterRegistrationBean<ServletGlobalCacheFilter> servletGlobalCacheFilterFilterRegistrationBean(@NotNull WebProperties properties) {
        log.debug("加载 Request & Response Cahce 过滤器 [{}]", ServletGlobalCacheFilter.class);
        ServletGlobalCacheFilter servletGlobalCacheFilter = new ServletGlobalCacheFilter(properties.getIgnoreCacheRequestUrl());
        FilterRegistrationBean<ServletGlobalCacheFilter> bean = new FilterRegistrationBean<>(servletGlobalCacheFilter);
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }

    /**
     * 注册跨域请求过滤器
     *
     * 跨域 CORS 配置，不是 prod 环境才允许跨域。
     * 在非生产环境下注册 CORS 过滤器，允许前端应用从不同域名访问后端 API。
     *
     * @return 配置好的CORS过滤器注册Bean
     * @since 1.0.0
     */
    @Bean
    @Profile(value = {App.ENV_NOT_PROD})
    @ConditionalOnMissingBean(CorsFilter.class)
    public FilterRegistrationBean<CorsFilter> corsFilterFilterRegistrationBean() {
        log.debug("非正式环境开启跨域支持: {}", CorsFilter.class);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration(StringPool.ANY_PATH, this.buildConfig());
        CorsFilter corsFilter = new CorsFilter(source);
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter);
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE + 40);
        return bean;
    }

    /**
     * 注册过滤器异常处理器
     *
     * Filter 异常处理器，用于捕获和处理过滤器层异常。
     * 当在过滤器链中发生异常时，该过滤器会统一处理这些异常。
     *
     * @param serverProperties 服务器配置属性，包含错误页面路径等配置
     * @return 配置好的异常过滤器注册Bean
     * @see ExceptionFilter 过滤器异常处理器实现
     * @see ServletErrorController 错误控制器
     * @see RestProperties#isEnableExceptionFilter 异常过滤器开关配置
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(ExceptionFilter.class)
    @ConditionalOnProperty(
        value = ConfigKey.WEB_ENABLE_EXCEPTION_FILTER,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public FilterRegistrationBean<ExceptionFilter> exceptionFilterFilterRegistrationBean(ServerProperties serverProperties) {
        log.debug("加载 Filter 异常处理器 [{}]", ExceptionFilter.class);
        FilterRegistrationBean<ExceptionFilter> bean = new FilterRegistrationBean<>(new ExceptionFilter(serverProperties));
        InnerWebUtils.setUrlPatterns(bean, Ordered.HIGHEST_PRECEDENCE + 50);
        return bean;
    }

    /**
     * 注册全局参数注入过滤器
     *
     * 参数注入过滤器，用于在请求处理过程中自动注入一些全局性的参数。
     *
     * @return 配置好的全局参数注入过滤器注册Bean
     * @see RestProperties#isEnableGlobalParameterFilter 全局参数注入过滤器开关配置
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(GlobalParameterFilter.class)
    @ConditionalOnProperty(
        value = ConfigKey.REST_ENABLE_GLOBAL_PARAMETER_FILTER,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public FilterRegistrationBean<GlobalParameterFilter> globalParameterFilterFilterRegistrationBean() {
        log.info("加载全局参数注入拦截器: {}", GlobalParameterFilter.class);
        FilterRegistrationBean<GlobalParameterFilter> bean = new FilterRegistrationBean<>(new GlobalParameterFilter());
        InnerWebUtils.setUrlPatterns(bean, Ordered.LOWEST_PRECEDENCE - 1000);
        return bean;
    }

    /**
     * 注册 XSS 防护过滤器
     *
     * 防 XSS 注入 Filter，用于防止跨站脚本攻击。
     * 该过滤器会对请求参数进行安全过滤，移除或转义潜在的恶意脚本代码。
     *
     * @param xssProperties XSS 防护配置属性，包含排除模式等配置
     * @return 配置好的XSS防护过滤器注册Bean
     * @see XssProperties#isEnableXssFilter XSS过滤器开关配置
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(XssFilter.class)
    @ConditionalOnProperty(
        value = ConfigKey.XSS_ENABLE_XSS_FILTER,
        havingValue = ConfigDefaultValue.TRUE_STRING,
        matchIfMissing = true
    )
    public FilterRegistrationBean<XssFilter> xssFilterFilterRegistrationBean(XssProperties xssProperties) {
        log.debug("加载防 XSS 注入 Filter: {}", XssFilter.class);
        FilterRegistrationBean<XssFilter> bean = new FilterRegistrationBean<>(new XssFilter(xssProperties.getExcludePatterns()));
        InnerWebUtils.setUrlPatterns(bean, Ordered.LOWEST_PRECEDENCE - 900);
        return bean;
    }

    /**
     * 注册自定义格式化器和转换器
     *
     * 前端传入的时间字符串，自动转换为 Date 类型，只针对普通的字段。
     * 如果是 @RequestBody 中的字段，将使用 {@link MappingApiJackson2HttpMessageConverter} 使用 jackson 进行转换。
     *
     * @param registry Spring MVC 的格式化注册表
     * @since 1.0.0
     */
    @Override
    public void addFormatters(@NotNull FormatterRegistry registry) {
        log.debug("注册 String -> Date 转换器 :[{}] 格式: [{}]", StringToDateConverter.class, StringToDateConverter.PATTERN);
        registry.addConverter(new StringToDateConverter());
        log.debug("注册通用枚举转换器: [{}]", GlobalEnumConverterFactory.class);
        registry.addConverterFactory(GLOBAL_ENUM_CONVERTER_FACTORY);
    }

    /**
     * 注册自定义参数解析器
     *
     * 向 Spring MVC 添加自定义的方法参数解析器，用于处理 Controller 方法中的特殊注解参数。
     *
     * @param argumentResolvers Spring MVC 的参数解析器列表
     * @since 1.0.0
     */
    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> argumentResolvers) {
        log.debug("注册 @RequestSingleParam 注解处理器: [{}]", RequestSingleParamHandlerMethodArgumentResolver.class);
        argumentResolvers.add(new RequestSingleParamHandlerMethodArgumentResolver(this.objectMapper, GLOBAL_ENUM_CONVERTER_FACTORY));
        argumentResolvers.add(new RequestAbstractFormMethodArgumentResolver(this.objectMapper, GLOBAL_ENUM_CONVERTER_FACTORY));
        argumentResolvers.add(new FormdataBodyArgumentResolver(this.objectMapper, GLOBAL_ENUM_CONVERTER_FACTORY));
        argumentResolvers.add(this.currentUserArgumentResolver);
    }

    /**
     * 配置 HTTP 消息转换器
     *
     * 使用 JACKSON 作为 JSON MessageConverter，配置 Spring MVC 的 HTTP 消息转换器。
     *
     * @param converters Spring MVC 的消息转换器列表
     * @since 1.0.0
     */
    @Override
    public void configureMessageConverters(@NotNull List<HttpMessageConverter<?>> converters) {
        log.debug("加载自定义消息增强转换器 [{}]", MappingApiJackson2HttpMessageConverter.class);
        converters.removeIf(x -> x instanceof StringHttpMessageConverter || x instanceof AbstractJackson2HttpMessageConverter);
        // Content-Type = text/plain 消息转换器, 强制使用 UTF-8
        converters.add(new StringHttpMessageConverter(Charsets.UTF_8));
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new BufferedImageHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new ResourceRegionHttpMessageConverter());

        this.config();

        converters.add(new MappingApiJackson2HttpMessageConverter(this.objectMapper));
    }

    /**
     * 配置自定义枚举序列化与反序列化
     *
     * 为 ObjectMapper 注册自定义的枚举序列化器和反序列化器。
     *
     * @since 1.0.0
     */
    private void config() {
        log.debug("加载枚举自定义序列化/反序列化处理器: [{}] [{}]", EntityEnumSerializer.class, EntityEnumDeserializer.class);
        SimpleModule simpleModule = new SimpleModule("EntityEnum-Converter", PackageVersion.VERSION);
        simpleModule.addDeserializer(SerializeEnum.class, new EntityEnumDeserializer<>());
        simpleModule.addSerializer(SerializeEnum.class, new EntityEnumSerializer<>());
        this.objectMapper.registerModule(simpleModule);
        this.objectMapper.findAndRegisterModules();
    }

    /**
     * 注册拦截器
     *
     * 添加拦截器，包括当前用户拦截器和认证拦截器。
     *
     * @param registry 拦截器注册表
     * @since 1.0.0
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        log.debug("加载响应结果包装拦截器: [{}]", CurrentUserInterceptor.class);

        registry.addInterceptor(this.currentUserInterceptor)
            .addPathPatterns(StringPool.ANY_PATH)
            .excludePathPatterns(new ArrayList<>(SecurityUtils.mergeSkipPatterns("")));

        log.debug("加载响应结果包装拦截器: [{}]", AuthenticationInterceptor.class);
        registry.addInterceptor(this.authenticationInterceptor)
            .addPathPatterns(StringPool.ANY_PATH)
            .excludePathPatterns(new ArrayList<>(SecurityUtils.mergeSkipPatterns("")));

    }

    /**
     * 配置路径匹配
     *
     * 开启矩阵变量 {@code @MatrixVariable} 支持。
     *
     * @param configurer 路径匹配配置器
     * @since 1.0.0
     */
    @Override
    public void configurePathMatch(@NotNull PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * 注册 Web MVC 配置
     *
     * 注册 RequestMappingHandlerMapping，不使用继承 WebMvcConfigurationSupport。
     * 替换后，会将其提供的一系列默认组件全部移除。
     *
     * @return Web MVC 注册配置
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean(WebMvcRegistrations.class)
    public WebMvcRegistrations registrations() {
        return new WebMvcRegistrations() {
            /**
             * 注册请求映射处理器
             *
             * 注册 RequestMappingHandlerMapping。
             *
             * @return 请求映射处理器
             * @since 1.0.0
             */
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new ApiVersionRequestMappingHandlerMapping();
            }
        };
    }


    /**
     * 构建 CORS 配置
     *
     * 构建跨域资源共享配置。
     *
     * @return CORS 配置
     * @since 1.0.0
     */
    private @NotNull CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1. 允许任何域名使用
        corsConfiguration.addAllowedOrigin(StringPool.ASTERISK);
        // 2. 允许任何头
        corsConfiguration.addAllowedHeader(StringPool.ASTERISK);
        // 3. 允许任何方法 (post、get等)
        corsConfiguration.addAllowedMethod(StringPool.ASTERISK);
        corsConfiguration.setMaxAge(MAX_AGE);
        corsConfiguration.setAllowCredentials(true);
        return corsConfiguration;
    }

}
