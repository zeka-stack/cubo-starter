package dev.dong4j.zeka.starter.dict.enums;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字典值状态枚举
 * <p> 用于表示字典值的启用或禁用状态, 包含两个状态: 禁用和启用
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@gmail.com"
 * @date 2025.12.22
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DictionaryValueState implements SerializeEnum<Integer> {
    /** 禁用状态, 对应值为 0, 描述为“禁用” */
    DISABLED(0, "禁用"),
    /** 启用状态 */
    ENABLED(1, "启用");

    /**
     * 字段值
     * <p> 用于表示枚举项对应的数值 </p>
     */
    private final Integer value;
    /**
     * 描述信息
     * <p> 表示枚举项的描述文本 </p>
     *
     * @see DictionaryValueState
     */
    private final String desc;
}
