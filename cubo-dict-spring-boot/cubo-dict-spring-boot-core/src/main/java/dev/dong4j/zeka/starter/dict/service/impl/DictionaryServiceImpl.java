package dev.dong4j.zeka.starter.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典服务实现类
 * <p> 该类实现了字典类型的增删改查以及字典值的管理, 支持缓存刷新和清除. 通过与字典类型和字典值的数据库映射进行交互,
 * 并在操作完成后发布相应的事件通知. 提供了多种方法用于获取字典类型和字典值, 并支持根据类型代码获取字典选项列表.
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
public class DictionaryServiceImpl implements DictionaryService {

    /** 字典类型映射器, 用于操作字典类型数据 */
    @Resource
    private DictionaryTypeMapper dictionaryTypeMapper;
    /**
     * 字典值映射器, 用于执行字典值相关的数据库操作
     *
     * @see DictionaryValueMapper
     */
    @Resource
    private DictionaryValueMapper dictionaryValueMapper;
    /** 字典缓存实例, 用于存储和管理字典类型及值的缓存数据 */
    @Resource
    private DictionaryCache dictionaryCache;
    /** 用于发布字典相关的事件, 如类型创建, 更新, 删除等操作 */
    @Resource
    private DictionaryEventPublisher eventPublisher;
    /** 缓存刷新延迟时间 (单位: 毫秒) */
    private final long cacheRefreshDelay;

    /**
     * 构造函数, 用于初始化字典服务实现类
     * <p> 设置缓存刷新延迟时间
     *
     * @param cacheRefreshDelay 缓存刷新延迟时间 (单位: 毫秒)
     */
    public DictionaryServiceImpl(long cacheRefreshDelay) {
        this.cacheRefreshDelay = cacheRefreshDelay;
    }

    // ==================== 字典类型管理 ====================

    /**
     * 保存字典类型
     * <p> 将字典类型插入数据库, 并在成功插入后发布类型创建事件. 同时更新缓存以移除该类型的旧数据.
     *
     * @param type 字典类型对象
     * @return 是否成功保存字典类型
     */
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

    /**
     * 更新字典类型信息
     * <p> 根据传入的字典类型对象更新数据库中的记录, 并发布更新事件. 如果更新成功, 则从缓存中移除对应的字典类型信息.
     *
     * @param type 字典类型对象
     * @return 如果更新成功返回 true, 否则返回 false
     */
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

    /**
     * 删除字典类型及其对应的字典值
     * <p> 根据给定的字典类型代码删除字典类型及其所有关联的字典值, 并更新缓存和事件发布
     *
     * @param typeCode 字典类型代码
     * @return 操作是否成功
     */
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

    /**
     * 根据字典类型编码获取字典类型信息
     * <p> 通过字典类型编码查询字典类型, 并且只返回状态为启用的类型信息
     *
     * @param typeCode 字典类型编码
     * @return 字典类型对象, 若未找到或状态不为启用则返回 null
     */
    @Override
    public DictionaryType getDictionaryType(String typeCode) {
        LambdaQueryWrapper<DictionaryType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryType::getCode, typeCode)
            .eq(DictionaryType::getState, DictionaryTypeState.ENABLED);
        return dictionaryTypeMapper.selectOne(wrapper);
    }

    /**
     * 获取所有启用的字典类型列表
     * <p> 根据状态筛选启用的字典类型, 并按顺序排序后返回
     *
     * @return 启用的字典类型列表
     */
    @Override
    public List<DictionaryType> listDictionaryTypes() {
        LambdaQueryWrapper<DictionaryType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryType::getState, DictionaryTypeState.ENABLED)
            .orderByAsc(DictionaryType::getOrder);
        return dictionaryTypeMapper.selectList(wrapper);
    }

    // ==================== 字典值管理 ====================

    /**
     * 保存字典值信息
     * <p> 将字典值对象持久化到数据库, 并清除相关缓存, 同时发布值创建事件.
     *
     * @param value 字典值对象, 包含类型编码, 编码, 名称等信息
     * @return 操作是否成功, 若插入记录数大于 0 则返回 true, 否则返回 false
     */
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

    /**
     * 更新字典值信息
     * <p> 根据提供的字典值对象更新数据库中的记录, 并在更新成功后清除缓存并发布更新事件.
     *
     * @param value 要更新的字典值对象
     * @return 更新是否成功, 成功返回 true, 否则返回 false
     */
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

    /**
     * 删除指定类型的字典值
     * <p> 根据给定的字典类型代码和字典值代码删除对应的字典值, 并更新缓存和发布事件.
     *
     * @param typeCode  字典类型代码
     * @param valueCode 字典值代码
     * @return 删除操作是否成功
     */
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

    /**
     * 根据类型代码获取字典值列表
     * <p> 首先从缓存中获取字典值, 如果缓存中不存在, 则从数据库查询并缓存结果. 只返回状态为启用的字典值, 并按排序顺序升序排列.
     *
     * @param typeCode 字典类型代码
     * @return 字典值列表, 若无数据则返回空列表
     */
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

    /**
     * 根据字典类型编码和值编码获取对应的字典值
     * <p> 通过指定的字典类型编码和值编码查询字典值, 并确保该值处于启用状态
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     * @return 对应的字典值对象, 若未找到或状态不为启用则返回 null
     */
    @Override
    public DictionaryValue getDictionaryValue(String typeCode, String valueCode) {
        LambdaQueryWrapper<DictionaryValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictionaryValue::getTypeCode, typeCode)
            .eq(DictionaryValue::getCode, valueCode)
            .eq(DictionaryValue::getState, DictionaryValueState.ENABLED);
        return dictionaryValueMapper.selectOne(wrapper);
    }

    // ==================== 前端接口 ====================

    /**
     * 根据字典类型编码获取对应的字典选项列表
     * <p> 通过指定的字典类型编码获取该类型下的所有字典值, 并将其转换为字典选项对象列表返回.
     *
     * @param typeCode 字典类型编码
     * @return 对应字典类型的选项列表, 若未找到相关字典值则返回空列表
     */
    @Override
    public List<DictionaryOption> getDictionaryOptions(String typeCode) {
        List<DictionaryValue> values = getDictionaryValues(typeCode);
        return values.stream()
            .map(this::convertToOption)
            .collect(Collectors.toList());
    }

    /**
     * 获取所有字典类型的选项列表
     * <p> 遍历所有启用的字典类型, 并为每个类型获取其对应的选项列表, 最终返回一个映射, 键为字典类型代码, 值为该类型的选项列表
     *
     * @return 包含所有字典类型及其选项列表的映射
     */
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

    /**
     * 刷新指定类型的字典缓存
     * <p> 移除指定类型的缓存数据, 重新加载字典值, 并发布刷新事件
     *
     * @param typeCode 字典类型编码
     */
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

    /**
     * 刷新所有字典缓存
     * <p> 清空当前缓存, 并重新加载所有字典类型的值到缓存中
     *
     * @since 1.0.0
     */
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

    /**
     * 清除指定类型的字典缓存
     * <p> 从缓存中移除指定类型代码的字典数据, 并记录日志信息
     *
     * @param typeCode 字典类型代码
     */
    @Override
    public void clearCache(String typeCode) {
        dictionaryCache.remove(typeCode);
        log.info("清除字典缓存: typeCode={}", typeCode);
    }

    /**
     * 清除所有字典缓存
     * <p> 移除字典缓存中的所有数据, 并记录日志信息
     *
     * @since 1.0.0
     */
    @Override
    public void clearAllCache() {
        dictionaryCache.clear();
        log.info("清除所有字典缓存");
    }

    // ==================== 私有方法 ====================

    /**
     * 将字典值对象转换为字典选项对象
     * <p> 根据字典值对象的属性, 创建并返回一个字典选项对象, 包括值, 标签, 描述, 禁用状态和排序顺序
     *
     * @param value 字典值对象
     * @return 字典选项对象
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
