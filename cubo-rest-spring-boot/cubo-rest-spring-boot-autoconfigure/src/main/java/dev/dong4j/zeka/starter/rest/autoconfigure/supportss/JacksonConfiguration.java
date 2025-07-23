package dev.dong4j.zeka.starter.rest.autoconfigure.supportss;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.jackson.JavaTimeModule;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.JsonUtils;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * <p>Description: Jackson配置类 </p>
 *
 * @author dong4j
 * @version 1.2.3
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:53
 * @since 1.0.0
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnEnabled(prefix = RestProperties.PREFIX)
@ConditionalOnClass(value = {ObjectMapper.class})
@EnableConfigurationProperties(JacksonProperties.class)
@AutoConfigureBefore(JacksonAutoConfiguration.class)
public class JacksonConfiguration implements ZekaAutoConfiguration {
    /** Jackson properties */
    private JacksonProperties jacksonProperties;

    /**
     * Jackson configuration
     *
     * @param jacksonPropertiesObjectProvider jackson properties object provider
     * @since 1.6.0
     */
    public JacksonConfiguration(@NotNull ObjectProvider<JacksonProperties> jacksonPropertiesObjectProvider) {
        log.info("启动自动配置: [{}]", this.getClass());
        if (jacksonPropertiesObjectProvider.getIfAvailable() != null) {
            this.jacksonProperties = jacksonPropertiesObjectProvider.getIfAvailable();
        }
    }

    /**
     * Object mapper object mapper.
     *
     * @return the object mapper
     * @since 1.0.0
     */
    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper globalObjectMapper = JsonUtils.getCopyMapper();
        if (this.jacksonProperties.getDefaultPropertyInclusion() != null) {
            globalObjectMapper.setSerializationInclusion(this.jacksonProperties.getDefaultPropertyInclusion());
        }
        // 添加 JDK8 的时间转换器
        globalObjectMapper.registerModule(new JavaTimeModule());
        globalObjectMapper.findAndRegisterModules();
        log.debug("初始化自定义 Jackson 配置 [{}]", globalObjectMapper);
        return globalObjectMapper;
    }

}
