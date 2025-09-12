package dev.dong4j.zeka.starter.dict;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
import dev.dong4j.zeka.starter.dict.enums.DictionaryValueState;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 字典服务测试
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Slf4j
@ZekaTest
class DictionaryServiceTest {

    @Resource
    private DictionaryService dictionaryService;

    @Test
    void testGetDictionaryOptions() {
        // 测试获取性别选项
        List<DictionaryOption> options = dictionaryService.getDictionaryOptions("gender");
        assertNotNull(options);
        assertFalse(options.isEmpty());

        log.info("性别选项: {}", options);

        // 验证选项结构
        for (DictionaryOption option : options) {
            assertNotNull(option.getValue());
            assertNotNull(option.getLabel());
        }
    }

    @Test
    void testGetAllDictionaryOptions() {
        // 测试获取所有字典选项
        Map<String, List<DictionaryOption>> allOptions = dictionaryService.getAllDictionaryOptions();
        assertNotNull(allOptions);

        log.info("所有字典选项: {}", allOptions);

        // 验证包含性别选项
        assertTrue(allOptions.containsKey("gender"));
    }

    @Test
    void testGetDictionaryValues() {
        // 测试获取字典值
        List<DictionaryValue> values = dictionaryService.getDictionaryValues("gender");
        assertNotNull(values);
        assertFalse(values.isEmpty());

        log.info("性别字典值: {}", values);

        // 验证字典值结构
        for (DictionaryValue value : values) {
            assertNotNull(value.getCode());
            assertNotNull(value.getName());
            assertEquals("gender", value.getTypeCode());
        }
    }

    @Test
    void testCacheManagement() {
        // 测试缓存管理
        dictionaryService.refreshCache("gender");
        dictionaryService.clearCache("gender");
        dictionaryService.refreshAllCache();
        dictionaryService.clearAllCache();

        log.info("缓存管理测试完成");
    }

    @Test
    void testDictionaryTypeOperations() {
        // 测试字典类型操作
        DictionaryType type = new DictionaryType();
        type.setCode("test_type");
        type.setName("测试类型");
        type.setDescription("测试描述");
        type.setState(DictionaryTypeState.ENABLED);
        type.setOrder(1);
        type.setTenantId("test");
        type.setClientId("test");

        // 保存字典类型
        boolean saved = dictionaryService.saveDictionaryType(type);
        assertTrue(saved);

        // 获取字典类型
        DictionaryType retrieved = dictionaryService.getDictionaryType("test_type");
        assertNotNull(retrieved);
        assertEquals("测试类型", retrieved.getName());

        // 更新字典类型
        type.setName("更新后的测试类型");
        boolean updated = dictionaryService.updateDictionaryType(type);
        assertTrue(updated);

        // 删除字典类型
        boolean deleted = dictionaryService.deleteDictionaryType("test_type");
        assertTrue(deleted);
    }

    @Test
    void testDictionaryValueOperations() {
        // 测试字典值操作
        DictionaryValue value = new DictionaryValue();
        value.setTypeCode("gender");
        value.setCode("test_value");
        value.setName("测试值");
        value.setDescription("测试描述");
        value.setState(DictionaryValueState.ENABLED);
        value.setOrder(99);
        value.setTenantId("test");
        value.setClientId("test");

        // 保存字典值
        boolean saved = dictionaryService.saveDictionaryValue(value);
        assertTrue(saved);

        // 获取字典值
        DictionaryValue retrieved = dictionaryService.getDictionaryValue("gender", "test_value");
        assertNotNull(retrieved);
        assertEquals("测试值", retrieved.getName());

        // 更新字典值
        value.setName("更新后的测试值");
        boolean updated = dictionaryService.updateDictionaryValue(value);
        assertTrue(updated);

        // 删除字典值
        boolean deleted = dictionaryService.deleteDictionaryValue("gender", "test_value");
        assertTrue(deleted);
    }
}
