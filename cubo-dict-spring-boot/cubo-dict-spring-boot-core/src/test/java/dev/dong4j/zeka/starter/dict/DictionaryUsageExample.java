package dev.dong4j.zeka.starter.dict;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典使用示例类
 * <p> 提供字典数据的查询, 验证及缓存管理功能, 主要针对性别字典项进行操作, 包括获取选项列表, 根据编码获取名称, 验证编码有效性以及管理缓存等.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class DictionaryUsageExample {

    /** 字典服务实例, 用于获取和管理字典数据 */
    private final DictionaryService dictionaryService;

    /**
     * 获取性别选项用于前端下拉框
     * <p> 调用字典服务获取性别相关的字典选项列表
     *
     * @return 性别选项列表
     */
    public List<DictionaryOption> getGenderOptions() {
        return dictionaryService.getDictionaryOptions("gender");
    }

    /**
     * 根据性别编码获取性别名称
     * <p> 从字典服务中获取 "gender" 类型的字典值列表, 查找与指定编码匹配的条目, 并返回其名称. 若未找到匹配项, 则返回 "未知".
     *
     * @param genderCode 性别编码
     * @return 匹配的性别名称, 若未找到则返回 "未知"
     */
    public String getGenderName(String genderCode) {
        List<DictionaryValue> values = dictionaryService.getDictionaryValues("gender");
        return values.stream()
            .filter(v -> v.getCode().equals(genderCode))
            .findFirst()
            .map(DictionaryValue::getName)
            .orElse("未知");
    }

    /**
     * 获取所有字典选项用于前端初始化
     * <p> 调用字典服务获取所有字典选项, 并以键值对的形式返回,
     * 其中键为字典类型的标识符, 值为该类型的字典选项列表.
     *
     * @return 包含所有字典类型的选项映射, 键为字典类型标识符, 值为对应的字典选项列表
     */
    public Map<String, List<DictionaryOption>> getAllOptions() {
        return dictionaryService.getAllDictionaryOptions();
    }

    /**
     * 验证性别编码是否有效
     * <p> 根据给定的性别编码, 检查是否存在对应的性别选项
     *
     * @param genderCode 性别编码
     * @return 如果存在对应的性别选项, 则返回 true; 否则返回 false
     */
    public boolean isValidGenderCode(String genderCode) {
        List<DictionaryValue> values = dictionaryService.getDictionaryValues("gender");
        return values.stream()
            .anyMatch(v -> v.getCode().equals(genderCode));
    }

    /**
     * 获取启用的性别选项
     * <p> 从字典服务中获取性别选项列表, 并过滤掉禁用的选项, 返回启用的性别选项列表
     *
     * @return 启用的性别选项列表
     */
    public List<DictionaryOption> getEnabledGenderOptions() {
        return dictionaryService.getDictionaryOptions("gender").stream()
            .filter(option -> !option.isDisabled())
            .toList();
    }

    /**
     * 管理字典缓存
     * <p> 用于刷新或清除指定字典类型的缓存, 以及刷新或清除所有字典缓存
     *
     */
    public void manageCache() {
        // 刷新指定字典类型缓存
        dictionaryService.refreshCache("gender");

        // 刷新所有缓存
        dictionaryService.refreshAllCache();

        // 清除指定字典类型缓存
        dictionaryService.clearCache("gender");

        // 清除所有缓存
        dictionaryService.clearAllCache();
    }
}
