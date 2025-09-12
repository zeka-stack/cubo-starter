package dev.dong4j.zeka.starter.dict;

import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 字典使用示例
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Slf4j
@Service
@AllArgsConstructor
public class DictionaryUsageExample {

    private final DictionaryService dictionaryService;

    /**
     * 示例1：获取性别选项用于前端下拉框
     */
    public List<DictionaryOption> getGenderOptions() {
        return dictionaryService.getDictionaryOptions("gender");
    }

    /**
     * 示例2：根据性别编码获取性别名称
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
     * 示例3：获取所有字典选项用于前端初始化
     */
    public Map<String, List<DictionaryOption>> getAllOptions() {
        return dictionaryService.getAllDictionaryOptions();
    }

    /**
     * 示例4：验证性别编码是否有效
     */
    public boolean isValidGenderCode(String genderCode) {
        List<DictionaryValue> values = dictionaryService.getDictionaryValues("gender");
        return values.stream()
            .anyMatch(v -> v.getCode().equals(genderCode));
    }

    /**
     * 示例5：获取启用的字典选项（排除禁用的）
     */
    public List<DictionaryOption> getEnabledGenderOptions() {
        return dictionaryService.getDictionaryOptions("gender").stream()
            .filter(option -> !option.isDisabled())
            .toList();
    }

    /**
     * 示例6：缓存管理
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
