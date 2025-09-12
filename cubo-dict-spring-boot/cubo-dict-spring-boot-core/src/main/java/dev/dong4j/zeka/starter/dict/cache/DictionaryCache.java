package dev.dong4j.zeka.starter.dict.cache;

import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import java.util.List;
import java.util.Map;

/**
 * 字典缓存接口
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
public interface DictionaryCache {

    /**
     * 存储字典值列表到缓存
     *
     * @param typeCode 字典类型编码
     * @param values   字典值列表
     * @since 1.0.0
     */
    void put(String typeCode, List<DictionaryValue> values);

    /**
     * 从缓存获取字典值列表
     *
     * @param typeCode 字典类型编码
     * @return 字典值列表
     * @since 1.0.0
     */
    List<DictionaryValue> get(String typeCode);

    /**
     * 从缓存中移除指定字典类型
     *
     * @param typeCode 字典类型编码
     * @since 1.0.0
     */
    void remove(String typeCode);

    /**
     * 清空所有缓存
     *
     * @since 1.0.0
     */
    void clear();

    /**
     * 是否启用缓存
     *
     * @return true-启用，false-禁用
     * @since 1.0.0
     */
    boolean isEnabled();

    /**
     * 批量存储所有字典数据
     *
     * @param allData 所有字典数据
     * @since 1.0.0
     */
    void putAll(Map<String, List<DictionaryValue>> allData);

    /**
     * 获取所有字典数据
     *
     * @return 所有字典数据
     * @since 1.0.0
     */
    Map<String, List<DictionaryValue>> getAll();
}
