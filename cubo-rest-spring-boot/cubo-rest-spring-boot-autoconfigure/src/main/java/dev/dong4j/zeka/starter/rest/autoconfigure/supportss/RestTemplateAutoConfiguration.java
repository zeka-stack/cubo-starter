package dev.dong4j.zeka.starter.rest.autoconfigure.supportss;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.exception.LowestException;
import dev.dong4j.zeka.kernel.common.jackson.MappingApiJackson2HttpMessageConverter;
import dev.dong4j.zeka.kernel.common.ssl.DisableValidationTrustManager;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.Charsets;
import dev.dong4j.zeka.kernel.common.util.HttpsUtils;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * <p>Description: </p>
 *
 * @author dong4j
 * @version 1.2.4
 * @email "mailto:dongshijie@gmail.com"
 * @date 2020.02.08 11:49
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(after = JacksonConfiguration.class)
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@EnableConfigurationProperties(RestProperties.class)
@ConditionalOnMissingClass("dev.dong4j.zeka.agent.adapter.config.AgentAdapterRestConfiguration")
public class RestTemplateAutoConfiguration implements ZekaAutoConfiguration {

    public RestTemplateAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 使用 okhttp, 忽略 https 证书
     *
     * 注意：在Spring Boot 3.x中，OkHttp3ClientHttpRequestFactory已被删除
     * 我们使用自定义的OkHttpClientHttpRequestFactory来替代
     * 支持连接池配置
     *
     * @param restProperties rest properties
     * @return the client http request factory
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnClass(OkHttpClient.class)
    public ClientHttpRequestFactory clientHttpRequestFactory(@NotNull RestProperties restProperties) {
        // 获取支持SSL的OkHttpClient基础配置
        OkHttpClient baseClient = this.getUnsafeOkHttpClient();

        // 使用自定义的OkHttpClientHttpRequestFactory，支持连接池配置
        // 注意：这里我们需要将SSL配置传递给自定义工厂
        return new OkHttpClientHttpRequestFactory(
            baseClient,  // 传递SSL配置的OkHttpClient
            restProperties.getConnectTimeout(),
            restProperties.getReadTimeout(),
            restProperties.getWriteTimeout(),
            restProperties.getConnectionPool().getMaxIdleConnections(),
            restProperties.getConnectionPool().getKeepAliveDuration(),
            TimeUnit.MINUTES
        );
    }

    /**
     * Rest template
     *
     * @param clientHttpRequestFactory client http request factory
     * @return the rest template
     * @since 1.0.0
     */
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(ClientHttpRequestFactory clientHttpRequestFactory,
                                     @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") ObjectMapper objectMapper) {
        RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory);
        this.converters(restTemplate, objectMapper);
        return restTemplate;
    }

    /**
     * Consumer rest template customizer
     *
     * @param listObjectProvider list object provider
     * @return the rest template customizer
     * @since 1.5.0
     */
    @Bean
    public RestTemplateCustomizer restConsumerRestTemplateCustomizer(
        ObjectProvider<List<ClientHttpRequestInterceptor>> listObjectProvider) {
        return restTemplate -> {
            Set<ClientHttpRequestInterceptor> set = new HashSet<>(restTemplate.getInterceptors());
            List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors = listObjectProvider.getIfAvailable();
            if (clientHttpRequestInterceptors != null && !clientHttpRequestInterceptors.isEmpty()) {
                set.addAll(clientHttpRequestInterceptors);
            }
            // 自带排序
            restTemplate.setInterceptors(new ArrayList<>(set));
        };
    }

    /**
     * 在 bean 实例化后对 bean 进行处理, 遍历每个 RestTemplate, 将自定义拦截器添加到 RestTemplate.
     *
     * @param restConsumerRestTemplateCustomizer rest consumer rest template customizer
     * @param restTemplate                       rest template
     * @return the smart initializing singleton
     * @since 1.8.0
     */
    @Bean
    public SmartInitializingSingleton restTemplateSmartInitializingSingleton(RestTemplateCustomizer restConsumerRestTemplateCustomizer,
                                                                             RestTemplate restTemplate) {
        return () -> restConsumerRestTemplateCustomizer.customize(restTemplate);
    }

    /**
     * 添加消息转换器
     *
     * @param restTemplate agent template
     * @since 1.0.0
     */
    private void converters(@NotNull RestTemplate restTemplate, ObjectMapper objectMapper) {
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(Charsets.UTF_8));
        // 设置 json 转换器
        MappingApiJackson2HttpMessageConverter messageConverter = new MappingApiJackson2HttpMessageConverter(objectMapper);
        // 删除默认的 MappingJackson2HttpMessageConverter 转换器
        Iterator<HttpMessageConverter<?>> httpMessageConverterIterator = restTemplate.getMessageConverters().iterator();
        while (httpMessageConverterIterator.hasNext()) {
            if (httpMessageConverterIterator.next().getClass().isAssignableFrom(MappingJackson2HttpMessageConverter.class)) {
                httpMessageConverterIterator.remove();
                break;
            }
        }
        // 使用自定义 MappingJackson2HttpMessageConverter 转换器
        restTemplate.getMessageConverters().add(messageConverter);
    }


    /**
     * Gets unsafe ok http client *
     *
     * @return the unsafe ok http client
     * @since 1.0.0
     */
    @NotNull
    private OkHttpClient getUnsafeOkHttpClient() {

        try {
            SSLContext sslContext = HttpsUtils.getSslContext();
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, DisableValidationTrustManager.INSTANCE);
            builder.hostnameVerifier((hostname, session) -> true);
            return builder.build();
        } catch (Exception e) {
            throw new LowestException(e);
        }

    }
}
