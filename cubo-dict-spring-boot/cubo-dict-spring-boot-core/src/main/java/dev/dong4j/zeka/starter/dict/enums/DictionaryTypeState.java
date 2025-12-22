package dev.dong4j.zeka.starter.dict.enums;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据字典类型状态枚举类
 * <p> 表示数据字典类型的启用状态, 包含禁用和启用两种状态
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 2.0.0
 */
@Getter
@AllArgsConstructor
public enum DictionaryTypeState implements SerializeEnum<Integer> {
    /** 禁用状态 */
    DISABLED(0, "禁用"),
    /**
     * 启用状态
     * <p> 表示字典类型表枚举中的启用状态, 值为 1
     *
     * @see DictionaryTypeState
     */
    ENABLED(1, "启用");

    /**
     * 枚举值对应的整数值
     *
     * @see DictionaryTypeState
     */
    private final Integer value;
    /**
     * 描述信息
     * <p> 表示枚举项的描述文本, 例如“禁用”或“启用”</p>
     */
    private final String desc;
}
