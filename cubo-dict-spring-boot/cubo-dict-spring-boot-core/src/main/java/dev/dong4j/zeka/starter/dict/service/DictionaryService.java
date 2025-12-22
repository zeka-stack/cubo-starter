package dev.dong4j.zeka.starter.dict.service;

import java.util.List;
import java.util.Map;

import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;

/**
 * 字典服务接口
 * <p> 提供字典类型和字典值的管理功能, 包括保存, 更新, 删除, 查询等操作, 同时支持缓存的刷新与清除, 适用于需要维护系统字典数据的场景.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
public interface DictionaryService {

    // ==================== 字典类型管理 ====================

    /**
     * 保存字典类型
     * <p> 用于持久化字典类型信息到数据库
     *
     * @param type 字典类型对象, 包含类型编码, 名称等信息
     * @return 操作是否成功
     * @since 1.0.0
     */
    boolean saveDictionaryType(DictionaryType type);

    /**
     * 更新字典类型
     * <p> 根据提供的字典类型对象更新字典类型信息
     *
     * @param type 字典类型对象
     * @return 是否更新成功
     * @since 1.0.0
     */
    boolean updateDictionaryType(DictionaryType type);

    /**
     * 删除字典类型
     * <p> 根据字典类型编码删除对应的字典类型
     *
     * @param typeCode 字典类型编码
     * @return 是否删除成功
     * @since 1.0.0
     */
    boolean deleteDictionaryType(String typeCode);

    /**
     * 根据字典类型编码获取字典类型
     * <p> 通过指定的字典类型编码查找对应的字典类型信息
     *
     * @param typeCode 字典类型编码
     * @return 字典类型对象, 如果未找到则返回 null
     */
    DictionaryType getDictionaryType(String typeCode);

    /**
     * 获取所有字典类型列表
     * <p> 返回系统中所有的字典类型信息
     *
     * @return 字典类型列表
     * @since 1.0.0
     */
    List<DictionaryType> listDictionaryTypes();

    // ==================== 字典值管理 ====================

    /**
     * 保存字典值
     * <p> 将给定的字典值保存到系统中
     *
     * @param value 字典值对象
     * @return 是否保存成功
     * @since 1.0.0
     */
    boolean saveDictionaryValue(DictionaryValue value);

    /**
     * 更新字典值
     * <p> 根据传入的字典值对象更新对应的字典值信息
     *
     * @param value 字典值对象
     * @return 是否更新成功
     * @since 1.0.0
     */
    boolean updateDictionaryValue(DictionaryValue value);

    /**
     * 删除字典值
     * <p> 根据字典类型编码和字典值编码删除对应的字典值
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     * @return 是否删除成功
     * @since 1.0.0
     */
    boolean deleteDictionaryValue(String typeCode, String valueCode);

    /**
     * 根据类型编码获取字典值列表
     * <p> 通过指定的字典类型编码查询对应的字典值列表
     *
     * @param typeCode 字典类型编码
     * @return 字典值列表
     */
    List<DictionaryValue> getDictionaryValues(String typeCode);

    /**
     * 根据类型编码和值编码获取字典值
     * <p> 通过给定的字典类型编码和字典值编码查找并返回对应的字典值对象
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     * @return 对应的字典值对象
     * @since 1.0.0
     */
    DictionaryValue getDictionaryValue(String typeCode, String valueCode);

    // ==================== 前端接口 ====================

    /**
     * 根据类型编码获取字典选项列表 (用于前端下拉框等)
     * <p> 通过指定的字典类型编码获取对应的字典选项列表, 通常用于前端展示下拉框等 UI 组件.
     *
     * @param typeCode 字典类型编码
     * @return 字典选项列表
     * @since 1.0.0
     */
    List<DictionaryOption> getDictionaryOptions(String typeCode);

    /**
     * 获取所有字典选项 (用于前端下拉框等)
     * <p> 返回一个映射, 其中键为字典类型编码, 值为对应的字典选项列表.
     *
     * @return 所有字典选项的映射, 键为类型编码, 值为选项列表
     * @since 1.0.0
     */
    Map<String, List<DictionaryOption>> getAllDictionaryOptions();

    // ==================== 缓存管理 ====================

    /**
     * 刷新指定字典类型缓存
     * <p> 根据字典类型编码刷新对应的缓存数据
     *
     * @param typeCode 字典类型编码
     */
    void refreshCache(String typeCode);

    /**
     * 刷新所有字典缓存
     * <p> 清除并重新加载所有字典类型的缓存数据
     *
     * @since 1.0.0
     */
    void refreshAllCache();

    /**
     * 清除指定字典类型的缓存
     *
     * @param typeCode 字典类型编码
     * @since 1.0.0
     */
    void clearCache(String typeCode);

    /**
     * 清除所有字典缓存
     * <p> 此方法用于清除系统中所有字典类型的缓存数据.
     *
     * @since 1.0.0
     */
    void clearAllCache();
}
