package dev.dong4j.zeka.starter.dict.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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

/**
 * 字典自动配置类
 * <p> 该类用于自动配置字典相关的服务和缓存组件. 根据配置文件中的属性值, 决定是否启用字典服务及其缓存类型, 并在启动时进行相应的初始化和预加载操作.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
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

    /**
     * 构造函数, 用于初始化字典自动配置类
     * <p> 在实例化时记录日志信息, 表示自动配置类已启动
     *
     */
    public DictAutoConfiguration() {
        log.info("启动自动配置: [{}]", this.getClass());
    }

    /**
     * 创建字典服务 Bean
     * <p> 当容器中未存在 DictionaryService 类型的 Bean 时, 创建并返回一个 DictionaryServiceImpl 实例.
     * 该服务使用配置中的缓存刷新延迟时间进行初始化.
     *
     * @param properties 配置属性对象, 用于获取缓存刷新延迟时间
     * @return 字典服务接口的实现实例
     */
    @Bean
    @ConditionalOnMissingBean
    public DictionaryService dictionaryService(DictProperties properties) {
        return new DictionaryServiceImpl(properties.getCacheRefreshDelay());
    }

    /**
     * 创建内存缓存实现
     * <p> 当配置项 zeka-stack.dict.cache-type 的值为 "memory" 时, 创建并返回一个内存缓存实例.
     *
     * @param properties 字典配置属性
     * @return 内存缓存接口实现
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zeka-stack.dict", name = "cache-type", havingValue = "memory")
    public DictionaryCache memoryDictionaryCache(DictProperties properties) {
        return new MemoryDictionaryCache(properties.getCacheExpireTime());
    }

    /**
     * 无操作缓存实现 (禁用缓存时使用)
     * <p> 当字典缓存类型设置为 "none" 时, 返回一个不执行任何操作的缓存实现.
     *
     * @return 无操作的字典缓存实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zeka-stack.dict", name = "cache-type", havingValue = "none")
    public DictionaryCache noOpDictionaryCache() {
        return new NoOpDictionaryCache();
    }

    /**
     * 字典事件监听器
     * <p> 用于监听字典相关的事件, 如字典数据变更等, 可进行相应的处理逻辑.
     *
     * @param dictionaryService 字典服务, 用于获取或操作字典数据
     * @param dictionaryCache   字典缓存, 用于缓存字典数据以提高访问效率
     * @return 字典事件监听器实例
     */
    @Bean
    @ConditionalOnMissingBean
    public DictionaryEventListener dictionaryEventListener(DictionaryService dictionaryService,
                                                           DictionaryCache dictionaryCache) {
        return new DictionaryEventListener(dictionaryService, dictionaryCache);
    }


    /**
     * 创建字典缓存预热器 Bean
     * <p> 仅当配置项 zeka-stack.dict.preload-cache 设置为 true 时创建该 Bean,
     * 用于在应用启动时预加载字典缓存数据.
     *
     * @param dictionaryService 字典服务, 用于获取字典数据
     * @param dictionaryCache   字典缓存, 用于存储预加载的字典数据
     * @return 字典缓存预热器实例
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "zeka-stack.dict", name = "preload-cache", havingValue = "true")
    public DictionaryCachePreloader dictionaryCachePreloader(DictionaryService dictionaryService,
                                                             DictionaryCache dictionaryCache) {
        return new DictionaryCachePreloader(dictionaryService, dictionaryCache);
    }

}
