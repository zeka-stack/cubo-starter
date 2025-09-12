package dev.dong4j.zeka.starter.dict.cache.impl;

import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 无操作字典缓存实现（禁用缓存时使用）
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
public class NoOpDictionaryCache implements DictionaryCache {

    @Override
    public void put(String typeCode, List<DictionaryValue> values) {
        // 无操作
    }

    @Override
    public List<DictionaryValue> get(String typeCode) {
        return null;
    }

    @Override
    public void remove(String typeCode) {
        // 无操作
    }

    @Override
    public void clear() {
        // 无操作
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void putAll(Map<String, List<DictionaryValue>> allData) {
        // 无操作
    }

    @Override
    public Map<String, List<DictionaryValue>> getAll() {
        return Collections.emptyMap();
    }
}
