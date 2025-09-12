package dev.dong4j.zeka.starter.dict.service;

import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import java.util.List;
import java.util.Map;

/**
 * 字典服务接口
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
public interface DictionaryService {

    // ==================== 字典类型管理 ====================

    /**
     * 保存字典类型
     *
     * @param type 字典类型
     * @return 是否成功
     * @since 1.0.0
     */
    boolean saveDictionaryType(DictionaryType type);

    /**
     * 更新字典类型
     *
     * @param type 字典类型
     * @return 是否成功
     * @since 1.0.0
     */
    boolean updateDictionaryType(DictionaryType type);

    /**
     * 删除字典类型
     *
     * @param typeCode 字典类型编码
     * @return 是否成功
     * @since 1.0.0
     */
    boolean deleteDictionaryType(String typeCode);

    /**
     * 根据编码获取字典类型
     *
     * @param typeCode 字典类型编码
     * @return 字典类型
     * @since 1.0.0
     */
    DictionaryType getDictionaryType(String typeCode);

    /**
     * 获取所有字典类型列表
     *
     * @return 字典类型列表
     * @since 1.0.0
     */
    List<DictionaryType> listDictionaryTypes();

    // ==================== 字典值管理 ====================

    /**
     * 保存字典值
     *
     * @param value 字典值
     * @return 是否成功
     * @since 1.0.0
     */
    boolean saveDictionaryValue(DictionaryValue value);

    /**
     * 更新字典值
     *
     * @param value 字典值
     * @return 是否成功
     * @since 1.0.0
     */
    boolean updateDictionaryValue(DictionaryValue value);

    /**
     * 删除字典值
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     * @return 是否成功
     * @since 1.0.0
     */
    boolean deleteDictionaryValue(String typeCode, String valueCode);

    /**
     * 根据类型编码获取字典值列表
     *
     * @param typeCode 字典类型编码
     * @return 字典值列表
     * @since 1.0.0
     */
    List<DictionaryValue> getDictionaryValues(String typeCode);

    /**
     * 根据类型编码和值编码获取字典值
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     * @return 字典值
     * @since 1.0.0
     */
    DictionaryValue getDictionaryValue(String typeCode, String valueCode);

    // ==================== 前端接口 ====================

    /**
     * 根据类型编码获取字典选项列表（用于前端下拉框等）
     *
     * @param typeCode 字典类型编码
     * @return 字典选项列表
     * @since 1.0.0
     */
    List<DictionaryOption> getDictionaryOptions(String typeCode);

    /**
     * 获取所有字典选项（用于前端下拉框等）
     *
     * @return 所有字典选项
     * @since 1.0.0
     */
    Map<String, List<DictionaryOption>> getAllDictionaryOptions();

    // ==================== 缓存管理 ====================

    /**
     * 刷新指定字典类型缓存
     *
     * @param typeCode 字典类型编码
     * @since 1.0.0
     */
    void refreshCache(String typeCode);

    /**
     * 刷新所有字典缓存
     *
     * @since 1.0.0
     */
    void refreshAllCache();

    /**
     * 清除指定字典类型缓存
     *
     * @param typeCode 字典类型编码
     * @since 1.0.0
     */
    void clearCache(String typeCode);

    /**
     * 清除所有字典缓存
     *
     * @since 1.0.0
     */
    void clearAllCache();
}
