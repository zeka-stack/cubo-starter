package dev.dong4j.zeka.starter.dict.cache;

import java.util.List;
import java.util.Map;

import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;

/**
 * 字典缓存接口
 * <p> 定义了字典数据的缓存操作规范, 包括字典数据的存储, 获取, 删除, 清空以及状态检查等功能, 适用于需要缓存字典信息的场景.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
public interface DictionaryCache {

    /**
     * 存储字典值列表到缓存
     * <p> 将指定类型的字典值列表存储到缓存中, 用于后续快速查询
     *
     * @param typeCode 字典类型编码, 用于标识字典类型
     * @param values   字典值列表, 包含需要存储的字典值对象
     */
    void put(String typeCode, List<DictionaryValue> values);

    /**
     * 从缓存获取字典值列表
     * <p> 根据指定的字典类型编码从缓存中获取对应的字典值列表.
     *
     * @param typeCode 字典类型编码
     * @return 字典值列表
     * @since 1.0.0
     */
    List<DictionaryValue> get(String typeCode);

    /**
     * 从缓存中移除指定字典类型
     * <p> 根据给定的字典类型编码, 从缓存中移除对应的字典类型及其相关数据
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
     * 判断是否启用缓存
     *
     * @return true 表示启用缓存,false 表示禁用缓存
     * @since 1.0.0
     */
    boolean isEnabled();

    /**
     * 批量存储所有字典数据
     * <p> 将所有字典数据存储到缓存中
     *
     * @param allData 所有字典数据, 键为字典类型编码, 值为字典值列表
     * @since 1.0.0
     */
    void putAll(Map<String, List<DictionaryValue>> allData);

    /**
     * 获取所有字典数据
     * <p> 返回缓存中的所有字典数据
     *
     * @return 所有字典数据, 键为字典类型编码, 值为对应的字典值列表
     * @since 1.0.0
     */
    Map<String, List<DictionaryValue>> getAll();
}
