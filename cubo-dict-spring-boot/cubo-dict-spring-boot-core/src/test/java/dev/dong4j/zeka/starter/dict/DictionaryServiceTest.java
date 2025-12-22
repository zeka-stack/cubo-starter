package dev.dong4j.zeka.starter.dict;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import dev.dong4j.zeka.kernel.test.ZekaTest;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
import dev.dong4j.zeka.starter.dict.enums.DictionaryValueState;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 字典服务测试类
 * <p> 用于测试字典服务的各种功能, 包括字典选项获取, 字典值获取, 缓存管理以及字典类型和值的增删改操作
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Slf4j
@ZekaTest
class DictionaryServiceTest {

    /** 字典服务实例, 用于测试字典相关操作 */
    @Resource
    private DictionaryService dictionaryService;

    /**
     * 测试获取字典选项功能
     * <p>
     * 测试场景: 请求性别字典选项
     * 预期结果: 应返回非空的选项列表, 且每个选项包含有效的值和标签
     * <p>
     * 该测试验证字典服务获取指定类型选项的功能是否正常工作
     */
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

    /**
     * 测试获取所有字典选项功能
     * <p>
     * 测试场景: 验证字典服务是否能正确获取所有字典选项
     * 预期结果: 应返回包含 "gender" 键的非空 Map
     * <p>
     * 该测试用例用于确保 getAllDictionaryOptions 方法能够正确加载并返回所有字典选项数据
     */
    @Test
    void testGetAllDictionaryOptions() {
        // 测试获取所有字典选项
        Map<String, List<DictionaryOption>> allOptions = dictionaryService.getAllDictionaryOptions();
        assertNotNull(allOptions);

        log.info("所有字典选项: {}", allOptions);

        // 验证包含性别选项
        assertTrue(allOptions.containsKey("gender"));
    }

    /**
     * 测试获取字典值功能
     * <p>
     * 测试场景: 获取 "gender" 类型的字典值列表
     * 预期结果: 应返回非空的字典值列表, 并且每个字典值的 code 和 name 都不能为空,typeCode 应为 "gender"
     */
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

    /**
     * 测试缓存管理功能
     * <p>
     * 测试场景: 刷新和清除特定字典类型的缓存, 以及刷新和清除所有字典类型的缓存
     * 预期结果: 成功执行缓存刷新和清除操作, 并记录日志确认测试完成
     */
    @Test
    void testCacheManagement() {
        // 测试缓存管理
        dictionaryService.refreshCache("gender");
        dictionaryService.clearCache("gender");
        dictionaryService.refreshAllCache();
        dictionaryService.clearAllCache();

        log.info("缓存管理测试完成");
    }

    /**
     * 测试字典类型的操作功能
     * <p>
     * 测试场景: 创建, 查询, 更新和删除字典类型
     * 预期结果: 创建操作应返回成功, 查询应获取到正确的类型信息, 更新操作应修改名称, 删除操作应成功移除类型
     */
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

    /**
     * 测试字典值的增删改操作功能
     * <p>
     * 测试场景: 验证字典值的保存, 查询, 更新和删除操作是否正常工作
     * 预期结果: 保存操作应返回 true, 查询应返回正确的字典值对象, 更新操作应返回 true, 删除操作应返回 true
     * <p>
     * 测试流程:
     * 1. 创建一个字典值对象并设置相关属性
     * 2. 调用保存方法, 验证是否保存成功
     * 3. 查询保存的字典值, 验证数据是否正确
     * 4. 更新字典值名称, 验证更新操作是否成功
     * 5. 删除字典值, 验证删除操作是否成功
     */
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
