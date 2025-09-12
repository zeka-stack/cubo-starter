package dev.dong4j.zeka.starter.dict.autoconfigure;

import dev.dong4j.zeka.kernel.autoconfigure.ZekaProperties;
import dev.dong4j.zeka.kernel.common.start.ZekaAutoConfiguration;
import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.cache.DictionaryCachePreloader;
import dev.dong4j.zeka.starter.dict.cache.impl.MemoryDictionaryCache;
import dev.dong4j.zeka.starter.dict.cache.impl.NoOpDictionaryCache;
import dev.dong4j.zeka.starter.dict.event.DictionaryEventListener;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import dev.dong4j.zeka.starter.dict.service.impl.DictionaryServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 字典组件自动装配类
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 22:49
 * @since 1.0.0
 */
@Slf4j
@AutoConfiguration
@ConditionalOnProperty(
    prefix = DictProperties.PREFIX,
    name = ZekaProperties.ENABLED,
    havingValue = ZekaProperties.ON,
    matchIfMissing = true)
@EnableConfigurationProperties(DictProperties.class)
public class DictAutoConfiguration implements ZekaAutoConfiguration {

    public DictAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 字典服务
     * 注意：由于DictionaryServiceImpl需要Mapper依赖，这里不直接创建Bean
     * 实际使用时需要在业务项目中通过@Service注解创建DictionaryServiceImpl
     *
     * @param properties 特性
     * @return 字典服务接口
     */
    @Bean
    @ConditionalOnMissingBean
    public DictionaryService dictionaryService(DictProperties properties) {
        return new DictionaryServiceImpl(properties.getCacheRefreshDelay());
    }

    /**
     * 内存缓存实现
     *
     * @param properties 特性
     * @return 字典缓存接口
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zeka-stack.dict", name = "cache-type", havingValue = "memory")
    public DictionaryCache memoryDictionaryCache(DictProperties properties) {
        return new MemoryDictionaryCache(properties.getCacheExpireTime());
    }

    /**
     * 无操作缓存实现（禁用缓存时使用）
     *
     * @return 字典缓存接口
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zeka-stack.dict", name = "cache-type", havingValue = "none")
    public DictionaryCache noOpDictionaryCache() {
        return new NoOpDictionaryCache();
    }

    /**
     * 字典事件监听器
     *
     * @param dictionaryService 词典服务
     * @param dictionaryCache   字典缓存
     * @return 字典事件监听器
     */
    @Bean
    @ConditionalOnMissingBean
    public DictionaryEventListener dictionaryEventListener(DictionaryService dictionaryService,
                                                           DictionaryCache dictionaryCache) {
        return new DictionaryEventListener(dictionaryService, dictionaryCache);
    }


    /**
     * 缓存预热器
     *
     * @param dictionaryService 词典服务
     * @param dictionaryCache   字典缓存
     * @return 字典缓存预热器
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zeka-stack.dict", name = "preload-cache", havingValue = "true")
    public DictionaryCachePreloader dictionaryCachePreloader(DictionaryService dictionaryService,
                                                             DictionaryCache dictionaryCache) {
        return new DictionaryCachePreloader(dictionaryService, dictionaryCache);
    }

}
