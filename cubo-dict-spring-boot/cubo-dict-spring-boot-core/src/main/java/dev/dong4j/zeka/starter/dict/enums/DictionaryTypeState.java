package dev.dong4j.zeka.starter.dict.enums;

import dev.dong4j.zeka.kernel.common.enums.SerializeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p> 字典类型表 枚举 </p>
 *
 * @author dong4j
 * @version 1.0.0
 * @email "mailto:dong4j@dong4j@gmail.com"
 * @date 2025.09.10 23:19
 * @since 1.0.0
 */
@Getter
@AllArgsConstructor
public enum DictionaryTypeState implements SerializeEnum<Integer> {
    /** DISABLED(0, "禁用"), */
    DISABLED(0, "禁用"),
    /** ENABLED(1, "启用"); */
    ENABLED(1, "启用");

    /** Value */
    private final Integer value;
    /** Desc */
    private final String desc;
}
