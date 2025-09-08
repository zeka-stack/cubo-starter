package dev.dong4j.zeka.starter.rest.autoconfigure.supportss;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dong4j.zeka.kernel.autoconfigure.condition.ConditionalOnEnabled;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.kernel.common.util.Jsons;
import dev.dong4j.zeka.kernel.web.jackson.JavaTimeModule;
import dev.dong4j.zeka.starter.rest.autoconfigure.RestProperties;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Jackson JSON 序列化配置类
 * <p>
 * 该配置类专门用于自定义和优化 Jackson ObjectMapper 的配置。
 * Jackson 是 Spring Boot 中默认的 JSON 序列化库，负责处理 HTTP 请求和响应
 * 中的 JSON 数据转换。这个配置类提供了统一的 JSON 处理策略。
 * <p>
 * 主要功能：
 * 1. 提供全局统一的 ObjectMapper 实例
 * 2. 集成 JDK 8 的日期时间 API 支持
 * 3. 自动配置序列化包含策略
 * 4. 支持自定义的 Jackson 模块注册
 * 5. 与 Spring Boot 的 Jackson 配置系统集成
 * <p>
 * 配置特点：
 * - 基于框架的全局 ObjectMapper 副本，保持一致性
 * - 支持 JDK 8 的 LocalDateTime、LocalDate 等新时间 API
 * - 遵循 Spring Boot 的 JacksonProperties 配置
 * - 自动注册和发现 Jackson 模块
 * - 使用 @Primary 注解确保优先级
 * <p>
 * 加载顺序：
 * 通过 @AutoConfiguration(before = JacksonAutoConfiguration.class) 确保
 * 在 Spring Boot 的 JacksonAutoConfiguration 之前加载，保证自定义配置的优先级。
 * <p>
 * 配置集成：
 * - JacksonProperties：读取 Spring Boot 的 Jackson 配置属性
 * - JavaTimeModule：支持 JDK 8 时间 API 的序列化
 * - Jsons.getCopyMapper()：获取框架的全局 ObjectMapper 副本
 * <p>
 * 使用场景：
 * - REST API 的 JSON 响应序列化
 * - HTTP 请求体的 JSON 反序列化
 * - 微服务间的 JSON 数据交换
 * - 数据库存储的 JSON 字段处理
 * - 缓存数据的序列化存储
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2020.01.27 14:53
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration(before = JacksonAutoConfiguration.class)
@ConditionalOnEnabled(value = RestProperties.PREFIX)
@ConditionalOnClass(value = {ObjectMapper.class})
@EnableConfigurationProperties(JacksonProperties.class)
public class JacksonConfiguration implements ZekaAutoConfiguration {
    /** Jackson properties */
    private JacksonProperties jacksonProperties;

    /**
     * Jackson 配置类构造函数
     * <p>
     * 初始化 Jackson 配置，并从 Spring Boot 的配置系统中获取 JacksonProperties。
     * 使用 ObjectProvider 进行延迟注入，避免循环依赖问题。
     *
     * @param jacksonPropertiesObjectProvider Jackson 属性对象提供者，用于获取 Spring Boot 的 Jackson 配置
     * @since 1.0.0
     */
    public JacksonConfiguration(@NotNull ObjectProvider<JacksonProperties> jacksonPropertiesObjectProvider) {
        log.info("启动自动配置: [{}]", this.getClass());
        if (jacksonPropertiesObjectProvider.getIfAvailable() != null) {
            this.jacksonProperties = jacksonPropertiesObjectProvider.getIfAvailable();
        }
    }

    /**
     * 创建自定义的 Jackson ObjectMapper
     * <p>
     * 该方法创建了一个全局使用的 ObjectMapper 实例，集成了框架的默认配置
     * 和自定义的功能增强。这个 ObjectMapper 会被 Spring 容器管理，
     * 并作为主要的 JSON 序列化工具在整个应用中使用。
     * <p>
     * 配置内容：
     * 1. 基础配置：
     * - 使用 Jsons.getCopyMapper() 获取框架的全局 ObjectMapper 副本
     * - 保持与框架其他组件的一致性
     * <p>
     * 2. 序列化包含策略：
     * - 读取 JacksonProperties 中的 defaultPropertyInclusion 配置
     * - 控制哪些字段参与 JSON 序列化（如排除 null 值字段）
     * <p>
     * 3. JDK 8 时间 API 支持：
     * - 注册 JavaTimeModule 模块
     * - 支持 LocalDateTime、LocalDate、Instant 等新时间类型
     * - 提供统一的日期时间序列化格式
     * <p>
     * 4. 模块自动发现：
     * - 调用 findAndRegisterModules() 自动注册类路径下的 Jackson 模块
     * - 支持第三方库的 Jackson 扩展
     * <p>
     * 注解说明：
     * - @Bean：将返回的 ObjectMapper 注册为 Spring Bean
     * - @Primary：设置为主要的 ObjectMapper，在多个候选者中优先使用
     *
     * @return 配置完成的 Jackson ObjectMapper 实例
     * @since 1.0.0
     */
    @Bean
    @Primary
    public ObjectMapper jacksonObjectMapper() {
        ObjectMapper globalObjectMapper = Jsons.getCopyMapper();
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
