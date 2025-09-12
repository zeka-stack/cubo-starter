package dev.dong4j.zeka.starter.dict.event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 字典事件发布器
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Slf4j
@Component
@AllArgsConstructor
public class DictionaryEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * 发布字典更新事件
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
     *
     * @param typeCode 字典类型编码
     * @param typeName 字典类型名称
     */
    public void publishTypeCreateEvent(String typeCode, String typeName) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.CREATE, "新增字典类型: " + typeName);
    }

    /**
     * 发布字典类型更新事件
     *
     * @param typeCode 字典类型编码
     * @param typeName 字典类型名称
     */
    public void publishTypeUpdateEvent(String typeCode, String typeName) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.UPDATE, "更新字典类型: " + typeName);
    }

    /**
     * 发布字典类型删除事件
     *
     * @param typeCode 字典类型编码
     */
    public void publishTypeDeleteEvent(String typeCode) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.DELETE, "删除字典类型: " + typeCode);
    }

    /**
     * 发布字典值新增事件
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
     *
     * @param typeCode  字典类型编码
     * @param valueCode 字典值编码
     */
    public void publishValueDeleteEvent(String typeCode, String valueCode) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.DELETE, "删除字典值: " + valueCode);
    }

    /**
     * 发布字典刷新事件
     *
     * @param typeCode 字典类型编码
     */
    public void publishRefreshEvent(String typeCode) {
        publishUpdateEvent(typeCode, DictionaryUpdateEvent.OperationType.REFRESH, "刷新字典缓存: " + typeCode);
    }
}
