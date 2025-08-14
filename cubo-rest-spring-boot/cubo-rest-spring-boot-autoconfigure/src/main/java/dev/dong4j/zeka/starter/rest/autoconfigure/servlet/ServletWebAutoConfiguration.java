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
 * <p>Description: WEB 初始化相关配置</p>
 * 1. 使用 MappingJackson2HttpMessageConverter 转换器
 * 2. 跨域请求设置
 * 3. 字符过滤器
 * 4. 接口日志
 *
 * @author dong4j
 * @version 1.2.3
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

    public ServletWebAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * Filter registration bean filter registration bean.
     * 设置字符过滤器
     *
     * @return the filter registration bean
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
     * request 和 response 缓存
     *
     * @param properties properties
     * @return the filter registration bean
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
     * Add cors mapping.
     * 跨域 CORS 配置, 不是 prod 环境才允许跨域
     *
     * @return the cors filter
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
     * Filter 异常处理
     *
     * @param serverProperties server properties
     * @return the filter registration bean
     * @see ExceptionFilter
     * @see ServletErrorController
     * @see RestProperties#isEnableExceptionFilter
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
     * 参数注入拦截器
     *
     * @return the filter registration bean
     * @see RestProperties#isEnableGlobalParameterFilter RestProperties#isEnableGlobalParameterFilter
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
     * 防 XSS 注入 Filter
     *
     * @param xssProperties xss properties
     * @return FilterRegistrationBean filter registration bean
     * @see XssProperties#isEnableXssFilter XssProperties#isEnableXssFilterXssProperties#isEnableXssFilter
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
     * 前端传入的时间字符串, 自动转换为 Date 类型, 只针对普通的字段,
     * 如果是 @RequestBody 中的字段, 将使用 {@link MappingApiJackson2HttpMessageConverter} 使用 jackson 进行转换
     *
     * @param registry the registry
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
     * Add argument resolvers *
     *
     * @param argumentResolvers argument resolvers
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
     * 使用 JACKSON 作为JSON MessageConverter
     *
     * @param converters converters
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
     * 自定义枚举序列化与反序列化方式
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
     * 添加拦截器
     *
     * @param registry registry
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
     * 开启矩阵变量 {@code @MatrixVariable}支持
     *
     * @param configurer configurer
     * @since 1.0.0
     */
    @Override
    public void configurePathMatch(@NotNull PathMatchConfigurer configurer) {
        UrlPathHelper urlPathHelper = new UrlPathHelper();
        urlPathHelper.setRemoveSemicolonContent(false);
        configurer.setUrlPathHelper(urlPathHelper);
    }

    /**
     * Registrations
     *
     * @return the web mvc registrations
     * @since 2.0.0
     */
    @Bean
    @ConditionalOnMissingBean(WebMvcRegistrations.class)
    public WebMvcRegistrations registrations() {
        return new WebMvcRegistrations() {
            /**
             * 注册 RequestMappingHandlerMapping,
             * 不使用 继承 WebMvcConfigurationSupport, 替换后，会将其提供的一系列默认组件全部移除。
             * 如我们注册拦截器使用的（RequestMappingHandlerAdapter）、全局异常拦截（ExceptionHandlerExceptionResolver）等
             *
             * @return the request mapping handler mapping
             * @since 2.0.0
             */
            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
                return new ApiVersionRequestMappingHandlerMapping();
            }
        };
    }


    /**
     * Build config cors configuration
     *
     * @return the cors configuration
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
