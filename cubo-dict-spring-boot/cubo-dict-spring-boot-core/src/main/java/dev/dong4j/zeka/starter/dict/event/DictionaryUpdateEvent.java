package dev.dong4j.zeka.starter.dict.event;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

/**
 * 字典更新事件类
 * <p> 用于在字典数据发生变更时发布事件, 包含变更类型, 操作类型及描述信息, 便于系统中其他组件监听并作出相应处理.
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Getter
public class DictionaryUpdateEvent extends ApplicationEvent {

    /** 字典类型编码 */
    private final String typeCode;

    /** 操作类型 */
    private final OperationType operationType;

    /**
     * 操作描述
     * <p> 描述当前操作的具体内容或原因
     */
    private final String description;

    /**
     * 构造函数, 用于创建一个新的字典更新事件对象
     * <p> 初始化字典更新事件的相关信息, 包括源对象, 字典类型编码, 操作类型和操作描述
     *
     * @param source        源对象, 通常为触发事件的对象
     * @param typeCode      字典类型编码
     * @param operationType 操作类型, 表示对字典进行的操作 (新增, 更新, 删除, 刷新)
     * @param description   操作描述, 提供关于操作的额外信息
     */
    public DictionaryUpdateEvent(Object source, String typeCode, OperationType operationType, String description) {
        super(source);
        this.typeCode = typeCode;
        this.operationType = operationType;
        this.description = description;
    }

    /**
     * 操作类型枚举
     * <p> 定义了系统中常见的操作类型, 用于标识不同的操作行为. 包括创建, 更新, 删除和刷新等操作.
     *
     * @author dong4j
     * @version 1.0.0
     * @email "mailto:dong4j@gmail.com"
     * @date 2025.12.22
     * @since 2.0.0
     */
    public enum OperationType {
        /** 操作类型: 新增 */
        CREATE,
        /** 更新操作类型 */
        UPDATE,
        /** 删除操作类型 */
        DELETE,
        /** 刷新操作类型 */
        REFRESH
    }
}
