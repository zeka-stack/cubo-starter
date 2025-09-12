package dev.dong4j.zeka.starter.dict.cache.impl;

import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * 内存字典缓存实现
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Slf4j
public class MemoryDictionaryCache implements DictionaryCache {

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
    private final long cacheExpireTime;

    public MemoryDictionaryCache(long cacheExpireTime) {
        this.cacheExpireTime = cacheExpireTime;
        startExpirationCleanup();
    }

    @Override
    public void put(String typeCode, List<DictionaryValue> values) {
        CacheEntry entry = new CacheEntry(values, System.currentTimeMillis() + cacheExpireTime * 1000);
        cache.put(typeCode, entry);
        log.debug("缓存字典数据: typeCode={}, size={}", typeCode, values.size());
    }

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

    @Override
    public void remove(String typeCode) {
        cache.remove(typeCode);
        log.debug("移除缓存字典数据: typeCode={}", typeCode);
    }

    @Override
    public void clear() {
        cache.clear();
        log.info("清空所有字典缓存");
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void putAll(Map<String, List<DictionaryValue>> allData) {

        allData.forEach(this::put);
        log.info("批量缓存字典数据: size={}", allData.size());
    }

    @Override
    public Map<String, List<DictionaryValue>> getAll() {
        Map<String, List<DictionaryValue>> result = new ConcurrentHashMap<>();
        long now = System.currentTimeMillis();

        cache.entrySet().stream()
            .filter(entry -> !entry.getValue().isExpired(now))
            .forEach(entry -> result.put(entry.getKey(), entry.getValue().values()));

        return result;
    }

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
     * 缓存条目
     */
    private record CacheEntry(List<DictionaryValue> values, long expireTime) {

        public boolean isExpired() {
            return isExpired(System.currentTimeMillis());
        }

        public boolean isExpired(long now) {
            return now > expireTime;
        }
    }
}
