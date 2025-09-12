package dev.dong4j.zeka.starter.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import dev.dong4j.zeka.starter.dict.cache.DictionaryCache;
import dev.dong4j.zeka.starter.dict.dao.DictionaryTypeMapper;
import dev.dong4j.zeka.starter.dict.dao.DictionaryValueMapper;
import dev.dong4j.zeka.starter.dict.entity.dto.DictionaryOption;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryType;
import dev.dong4j.zeka.starter.dict.entity.po.DictionaryValue;
import dev.dong4j.zeka.starter.dict.enums.DictionaryTypeState;
import dev.dong4j.zeka.starter.dict.enums.DictionaryValueState;
import dev.dong4j.zeka.starter.dict.event.DictionaryEventPublisher;
import dev.dong4j.zeka.starter.dict.service.DictionaryService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 字典服务实现类
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
public class DictionaryServiceImpl implements DictionaryService {

    @Resource
    private DictionaryTypeMapper dictionaryTypeMapper;
    @Resource
    private DictionaryValueMapper dictionaryValueMapper;
    @Resource
    private DictionaryCache dictionaryCache;
    @Resource
    private DictionaryEventPublisher eventPublisher;
    private final long cacheRefreshDelay;

    public DictionaryServiceImpl(long cacheRefreshDelay) {
        this.cacheRefreshDelay = cacheRefreshDelay;
    }

    // ==================== 字典类型管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDictionaryType(DictionaryType type) {
        // 1. 先删除缓存
        dictionaryCache.remove(type.getCode());

        // 2. 保存到数据库
        int result = dictionaryTypeMapper.insert(type);

        // 3. 发布事件
        if (result > 0) {
            eventPublisher.publishTypeCreateEvent(type.getCode(), type.getName());
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictionaryType(DictionaryType type) {
        // 1. 先删除缓存
        dictionaryCache.remove(type.getCode());

        // 2. 更新数据库
        int result = dictionaryTypeMapper.updateById(type);

        // 3. 发布事件
        if (result > 0) {
            eventPublisher.publishTypeUpdateEvent(type.getCode(), type.getName());
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictionaryType(String typeCode) {
        // 1. 先删除缓存
        dictionaryCache.remove(typeCode);

        // 2. 删除字典值
        LambdaQueryWrapper<DictionaryValue> valueWrapper = new LambdaQueryWrapper<>();
        valueWrapper.eq(DictionaryValue::getTypeCode, typeCode);
        dictionaryValueMapper.delete(valueWrapper);

        // 3. 删除字典类型
        LambdaQueryWrapper<DictionaryType> typeWrapper = new LambdaQueryWrapper<>();
        typeWrapper.eq(DictionaryType::getCode, typeCode);
        int result = dictionaryTypeMapper.delete(typeWrapper);

        // 4. 发布事件
        if (result > 0) {
            eventPublisher.publishTypeDeleteEvent(typeCode);
        }

        return result > 0;
    }

    @Override
    public DictionaryType getDictionaryType(String typeCode) {
        LambdaQueryWrapper<DictionaryType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryType::getCode, typeCode)
            .eq(DictionaryType::getState, DictionaryTypeState.ENABLED);
        return dictionaryTypeMapper.selectOne(wrapper);
    }

    @Override
    public List<DictionaryType> listDictionaryTypes() {
        LambdaQueryWrapper<DictionaryType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryType::getState, DictionaryTypeState.ENABLED)
            .orderByAsc(DictionaryType::getOrder);
        return dictionaryTypeMapper.selectList(wrapper);
    }

    // ==================== 字典值管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDictionaryValue(DictionaryValue value) {
        // 1. 先删除缓存
        dictionaryCache.remove(value.getTypeCode());

        // 2. 保存到数据库
        int result = dictionaryValueMapper.insert(value);

        // 3. 发布事件
        if (result > 0) {
            eventPublisher.publishValueCreateEvent(value.getTypeCode(), value.getCode(), value.getName());
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictionaryValue(DictionaryValue value) {
        // 1. 先删除缓存
        dictionaryCache.remove(value.getTypeCode());

        // 2. 更新数据库
        int result = dictionaryValueMapper.updateById(value);

        // 3. 发布事件
        if (result > 0) {
            eventPublisher.publishValueUpdateEvent(value.getTypeCode(), value.getCode(), value.getName());
        }

        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictionaryValue(String typeCode, String valueCode) {
        // 1. 先删除缓存
        dictionaryCache.remove(typeCode);

        // 2. 删除字典值
        LambdaQueryWrapper<DictionaryValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryValue::getTypeCode, typeCode)
            .eq(DictionaryValue::getCode, valueCode);
        int result = dictionaryValueMapper.delete(wrapper);

        // 3. 发布事件
        if (result > 0) {
            eventPublisher.publishValueDeleteEvent(typeCode, valueCode);
        }

        return result > 0;
    }

    @Override
    public List<DictionaryValue> getDictionaryValues(String typeCode) {
        // 1. 先尝试从缓存获取
        List<DictionaryValue> cachedValues = dictionaryCache.get(typeCode);
        if (cachedValues != null) {
            return cachedValues;
        }

        // 2. 从数据库查询
        LambdaQueryWrapper<DictionaryValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryValue::getTypeCode, typeCode)
            .eq(DictionaryValue::getState, DictionaryValueState.ENABLED)
            .orderByAsc(DictionaryValue::getOrder);
        List<DictionaryValue> values = dictionaryValueMapper.selectList(wrapper);

        // 3. 存入缓存
        if (!values.isEmpty()) {
            dictionaryCache.put(typeCode, values);
        }

        return values;
    }

    @Override
    public DictionaryValue getDictionaryValue(String typeCode, String valueCode) {
        LambdaQueryWrapper<DictionaryValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryValue::getTypeCode, typeCode)
            .eq(DictionaryValue::getCode, valueCode)
            .eq(DictionaryValue::getState, DictionaryValueState.ENABLED);
        return dictionaryValueMapper.selectOne(wrapper);
    }

    // ==================== 前端接口 ====================

    @Override
    public List<DictionaryOption> getDictionaryOptions(String typeCode) {
        List<DictionaryValue> values = getDictionaryValues(typeCode);
        return values.stream()
            .map(this::convertToOption)
            .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<DictionaryOption>> getAllDictionaryOptions() {
        List<DictionaryType> types = listDictionaryTypes();
        return types.stream()
            .collect(Collectors.toMap(
                DictionaryType::getCode,
                type -> getDictionaryOptions(type.getCode())
            ));
    }

    // ==================== 缓存管理 ====================

    @Override
    public void refreshCache(String typeCode) {
        // 清除缓存
        dictionaryCache.remove(typeCode);
        // 重新加载
        getDictionaryValues(typeCode);
        // 发布事件
        eventPublisher.publishRefreshEvent(typeCode);
        log.info("刷新字典缓存: typeCode={}", typeCode);
    }

    @Override
    public void refreshAllCache() {
        // 清除所有缓存
        dictionaryCache.clear();
        // 重新加载所有数据
        List<DictionaryType> types = listDictionaryTypes();
        for (DictionaryType type : types) {
            getDictionaryValues(type.getCode());
        }
        log.info("刷新所有字典缓存: size={}", types.size());
    }

    @Override
    public void clearCache(String typeCode) {
        dictionaryCache.remove(typeCode);
        log.info("清除字典缓存: typeCode={}", typeCode);
    }

    @Override
    public void clearAllCache() {
        dictionaryCache.clear();
        log.info("清除所有字典缓存");
    }

    // ==================== 私有方法 ====================

    /**
     * 转换字典值为选项
     */
    private DictionaryOption convertToOption(DictionaryValue value) {
        return new DictionaryOption()
            .setValue(value.getCode())
            .setLabel(value.getName())
            .setDescription(value.getDescription())
            .setDisabled(value.getState() != DictionaryValueState.ENABLED)
            .setSortOrder(value.getOrder());
    }
}
