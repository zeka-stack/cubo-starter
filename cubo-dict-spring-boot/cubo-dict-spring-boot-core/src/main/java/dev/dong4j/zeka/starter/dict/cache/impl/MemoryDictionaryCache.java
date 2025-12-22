package dev.dong4j.zeka.starter.dict.cache.impl;

import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import lombok.extern.slf4j.Slf4j;

/**
 * 内存字典缓存实现类
 * <p> 提供基于内存的字典数据缓存功能, 支持数据的存储, 获取, 删除, 清空以及自动过期清理
 * <p> 该类使用 ConcurrentHashMap 实现线程安全的缓存存储, 并通过定时任务定期清理过期缓存数据
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
public class MemoryDictionaryCache implements DictionaryCache {

    /**
     * 内存字典缓存的存储结构
     * <p> 使用 ConcurrentHashMap 存储字典缓存条目, 键为字典类型代码, 值为 CacheEntry 对象
     *
     * @see ConcurrentHashMap
     * @see CacheEntry
     */
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    /** 定时任务调度器, 用于执行缓存过期清理任务 */
    @SuppressWarnings("PMD.ThreadPoolCreationRule")
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    /** 缓存过期时间, 单位为秒 */
    private final long cacheExpireTime;

    /**
     * 构造函数, 初始化内存字典缓存
     * <p> 设置缓存过期时间并启动过期缓存清理任务
     *
     * @param cacheExpireTime 缓存条目的过期时间 (单位: 秒)
     */
    public MemoryDictionaryCache(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
        startExpirationCleanup();
    }

    /**
     * 将指定类型的字典数据存入缓存
     * <p> 创建一个缓存条目并将其放入内存缓存中, 缓存条目包含字典数据和过期时间
     *
     * @param typeCode 字典类型编码
     * @param values   要缓存的字典数据列表
     */
    @Override
    public void put(String typeCode, List<DictionaryValue> values) {
        CacheEntry entry = new CacheEntry(values, System.currentTimeMillis() + cacheExpireTime * 1000);
        cache.put(typeCode, entry);
        log.debug("缓存字典数据: typeCode={}, size={}", typeCode, values.size());
    }

    /**
     * 根据类型代码从缓存中获取字典值列表
     * <p> 从缓存中查找指定类型的字典数据, 如果缓存条目不存在或已过期, 则返回 null 并从缓存中移除该条目.
     *
     * @param typeCode 类型代码, 用于标识字典数据的类型
     * @return 对应类型的字典值列表, 如果不存在或已过期则返回 null
     */
    @Override
    public List<DictionaryValue> get(String typeCode) {

        CacheEntry entry = cache.get(typeCode);
        if (entry == null || entry.isExpired()) {
            if (entry != null) {
                cache.remove(typeCode);
            }
            return null;
        }

        log.debug("从缓存获取字典数据: typeCode={}, size={}", typeCode, entry.values().size());
        return entry.values();
    }

    /**
     * 从缓存中移除指定类型的字典数据
     * <p> 根据传入的类型编码, 从内存字典缓存中删除对应的缓存条目, 并记录调试日志
     *
     * @param typeCode 要移除的字典类型编码
     */
    @Override
    public void remove(String typeCode) {
        cache.remove(typeCode);
        log.debug("移除缓存字典数据: typeCode={}", typeCode);
    }

    /**
     * 清空所有字典缓存
     * <p> 移除缓存中所有存储的字典数据.
     *
     * @since 1.0.0
     */
    @Override
    public void clear() {
        cache.clear();
        log.info("清空所有字典缓存");
    }

    /**
     * 检查缓存是否启用
     * <p> 此方法用于检查当前缓存是否处于启用状态. 对于内存字典缓存, 该方法始终返回 true, 表示缓存总是启用的.
     *
     * @return 布尔值, 表示缓存是否启用
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 批量缓存字典数据
     * <p> 将传入的字典数据批量缓存到内存中, 每个条目会根据设置的过期时间进行管理
     *
     * @param allData 要缓存的字典数据, 键为类型编码, 值为对应的字典值列表
     */
    @Override
    public void putAll(Map<String, List<DictionaryValue>> allData) {

        allData.forEach(this::put);
        log.info("批量缓存字典数据: size={}", allData.size());
    }

    /**
     * 获取所有未过期的字典缓存数据
     * <p> 遍历缓存中的所有条目, 过滤掉已过期的数据, 并将未过期的数据放入结果集中返回
     *
     * @return 包含所有未过期字典数据的映射表, 键为字典类型代码, 值为对应的字典值列表
     */
    @Override
    public Map<String, List<DictionaryValue>> getAll() {
        Map<String, List<DictionaryValue>> result = Maps.newConcurrentMap();
        long now = System.currentTimeMillis();

        cache.entrySet().stream()
            .filter(entry -> !entry.getValue().isExpired(now))
            .forEach(entry -> result.put(entry.getKey(), entry.getValue().values()));

        return result;
    }

    /**
     * 启动缓存过期清理任务
     * <p> 定时清理已过期的缓存条目, 每隔 60 秒执行一次
     *
     * @since 1.0.0
     */
    private void startExpirationCleanup() {
        scheduler.scheduleWithFixedDelay(() -> {
            long now = System.currentTimeMillis();
            int removedCount = 0;

            var iterator = cache.entrySet().iterator();
            while (iterator.hasNext()) {
                var entry = iterator.next();
                if (entry.getValue().isExpired(now)) {
                    iterator.remove();
                    removedCount++;
                }
            }

            if (removedCount > 0) {
                log.debug("清理过期缓存: count={}", removedCount);
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    /**
     * 缓存条目记录类
     * <p> 用于存储缓存项及其过期时间. 包含缓存值列表和过期时间, 并提供了判断缓存是否过期的方法.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.22
     * @since 2.0.0
     */
    private record CacheEntry(List<DictionaryValue> values, long expireTime) {

        /**
         * 检查当前缓存条目是否过期
         * <p> 通过比较当前时间与缓存条目的过期时间来判断是否过期
         *
         * @return 如果缓存条目已过期, 则返回 true; 否则返回 false
         */
        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }

        /**
         * 判断缓存条目是否已过期
         * <p> 根据当前时间与缓存条目的过期时间进行比较, 判断是否已过期.
         *
         * @param now 当前时间 (毫秒)
         * @return 如果当前时间大于过期时间则返回 true, 否则返回 false
         */
        public boolean isExpired(long now) {
            return now > expireTime;
        }
    }
}
