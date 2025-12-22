package dev.dong4j.zeka.starter.dict.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 字典事件发布器
 * <p> 用于发布字典相关的更新事件, 包括字典类型和字典值的创建, 更新, 删除以及刷新操作. 通过事件机制通知相关组件进行数据同步或缓存更新.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class DictionaryEventPublisher {

    /**
     * 应用事件发布器, 用于发布各种字典相关的事件
     *
     * @see ApplicationEventPublisher
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 发布字典更新事件
     * <p> 创建并发布一个字典更新事件, 记录日志信息
     *
     * @param typeCode      字典类型编码
     * @param operationType 操作类型
     * @param description   操作描述
     */
    public void publishUpdateEvent(String typeCode, DictionaryUpdateEvent.OperationType operationType, String description) {
        DictionaryUpdateEvent event = new DictionaryUpdateEvent(this, typeCode, operationType, description);
        eventPublisher.publishEvent(event);
        log.debug("发布字典更新事件: typeCode={}, operationType={}, description={}", typeCode, operationType, description);
    }

    /**
     * 发布字典类型新增事件
     * <p> 通过指定的字典类型编码和名称, 发布一个表示字典类型新增操作的事件.
     *
     * @param typeCode 字典类型编码
     * @param typeName 字典类型名称
     */
    public void publishTypeCreateEvent(String typeCode, String typeName) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.CREATE, "新增字典类型: " + typeName);
    }

    /**
     * 发布字典类型更新事件
     * <p> 调用 {@link #publishUpdateEvent} 方法发布字典更新事件, 操作类型为更新, 并附带字典类型的名称作为描述信息
     *
     * @param typeCode 字典类型编码
     * @param typeName 字典类型名称
     */
    public void publishTypeUpdateEvent(String typeCode, String typeName) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.UPDATE, "更新字典类型: " + typeName);
    }

    /**
     * 发布字典类型删除事件
     * <p> 根据给定的字典类型编码发布删除事件
     *
     * @param typeCode 字典类型编码
     */
    public void publishTypeDeleteEvent(String typeCode) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.DELETE, "删除字典类型: " + typeCode);
    }

    /**
     * 发布字典值新增事件
     * <p> 用于发布字典值新增操作的事件, 包含字典类型编码, 字典值编码和字典值名称信息.
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     * @param valueName 字典值名称
     */
    public void publishValueCreateEvent(String typeCode, String valueCode, String valueName) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.CREATE, "新增字典值: " + valueName);
    }

    /**
     * 发布字典值更新事件
     * <p> 通过字典类型编码, 字典值编码和字典值名称发布字典值更新事件
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     * @param valueName 字典值名称
     */
    public void publishValueUpdateEvent(String typeCode, String valueCode, String valueName) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.UPDATE, "更新字典值: " + valueName);
    }

    /**
     * 发布字典值删除事件
     * <p> 根据字典类型编码和字典值编码发布字典值删除事件
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     */
    public void publishValueDeleteEvent(String typeCode, String valueCode) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.DELETE, "删除字典值: " + valueCode);
    }

    /**
     * 发布字典刷新事件
     * <p> 通过字典类型编码发布字典刷新事件, 用于通知系统刷新对应类型的字典缓存
     *
     * @param typeCode 字典类型编码
     */
    public void publishRefreshEvent(String typeCode) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.REFRESH, "刷新字典缓存: " + typeCode);
    }
}
