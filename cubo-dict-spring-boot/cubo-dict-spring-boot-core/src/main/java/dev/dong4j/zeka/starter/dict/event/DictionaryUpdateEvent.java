package dev.dong4j.zeka.starter.dict.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 字典更新事件
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.09.10 23:30
 * @since 1.0.0
 */
@Getter
public class DictionaryUpdateEvent extends ApplicationEvent {

    /** 字典类型编码 */
    private final String typeCode;

    /** 操作类型 */
    private final OperationType operationType;

    /** 操作描述 */
    private final String description;

    public DictionaryUpdateEvent(Object source, String typeCode, OperationType operationType, String description) {
        super(source);
        this.typeCode = typeCode;
        this.operationType = operationType;
        this.description = description;
    }

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        /** 新增 */
        CREATE,
        /** 更新 */
        UPDATE,
        /** 删除 */
        DELETE,
        /** 刷新 */
        REFRESH
    }
}
