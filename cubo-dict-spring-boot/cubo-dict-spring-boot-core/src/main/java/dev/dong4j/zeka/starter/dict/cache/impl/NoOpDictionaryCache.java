package dev.dong4j.zeka.starter.dict.cache.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;

/**
 * 空操作字典缓存实现类
 * <p> 提供一个不执行任何实际缓存操作的字典缓存实现, 适用于测试或禁用缓存功能的场景
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
public class NoOpDictionaryCache implements DictionaryCache {

    /**
     * 将指定类型的字典值列表放入缓存中
     * <p> 此方法在无操作字典缓存实现中被重写, 实际不会执行任何操作
     *
     * @param typeCode 字典类型代码
     * @param values   字典值列表
     * @since 1.0.0
     */
    @Override
    public void put(String typeCode, List<DictionaryValue> values) {
        // 无操作
    }

    /**
     * 根据类型代码获取字典值列表
     * <p> 在无操作缓存实现中, 该方法始终返回 null, 表示未找到对应类型的字典值.
     *
     * @param typeCode 类型代码
     * @return 对应类型的字典值列表, 若未找到则返回 null
     */
    @Override
    public List<DictionaryValue> get(String typeCode) {
        return null;
    }

    /**
     * 移除指定类型的字典数据
     * <p> 该方法用于从缓存中移除与给定类型代码关联的字典值, 由于当前为无操作缓存实现, 此方法不执行任何实际操作.
     *
     * @param typeCode 要移除的字典类型代码
     */
    @Override
    public void remove(String typeCode) {
        // 无操作
    }

    /**
     * 清除字典缓存中的所有数据
     * <p> 该方法用于清除字典缓存中存储的所有条目, 适用于禁用缓存时的清理操作.
     *
     * @since 1.0.0
     */
    @Override
    public void clear() {
        // 无操作
    }

    /**
     * 检查字典缓存是否启用
     * <p> 此方法用于判断当前字典缓存是否启用. 由于这是一个无操作的字典缓存实现,
     * 因此该方法总是返回 false, 表示缓存未启用.
     *
     * @return 始终返回 false, 表示缓存未启用
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

    /**
     * 将所有字典数据放入缓存
     * <p> 此方法在无操作缓存实现中不执行任何操作, 用于兼容接口定义.</p>
     *
     * @param allData 包含字典类型码到字典值列表的映射
     */
    @Override
    public void putAll(Map<String, List<DictionaryValue>> allData) {
        // 无操作
    }

    /**
     * 获取所有字典数据
     * <p> 返回一个不可变的空映射, 表示当前缓存未启用或没有可用的数据.
     *
     * @return 所有字典数据的映射, 返回一个不可变的空映射
     */
    @Override
    public Map<String, List<DictionaryValue>> getAll() {
        return Collections.emptyMap();
    }
}
